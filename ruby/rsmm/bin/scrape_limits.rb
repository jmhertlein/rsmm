#!/usr/bin/env ruby

require 'date'
require 'yaml'
require "json"
require "net/http"
require "uri"
require 'open-uri'
require 'pg'
require 'nokogiri'
require 'mail'
require 'rsmm/scrape/html/table'
require 'rsmm/db'
require 'rsmm/config'
require 'rsmm/scrape/rs_type_scrapers'
require 'opts4j4r'

LIMITS_URL_FOR_RS_TYPE = {rs3: "http://runescape.wikia.com/wiki/Calculator:Grand_Exchange_buying_limits", osrs: "http://oldschoolrunescape.wikia.com/wiki/Grand_Exchange/Buying_limits" }

start_ts = DateTime.now

options = Opts4J4R::parse do |opts|
  opts.sym! :mode, "One of [prod, dev]."
  opts.sym! :rs_type, "One of [osrs, rs3]."
  opts.flag :show_non_matching, "Include list of non-matching items in stdout output."
end

mode = options[:mode]
rs_type = options[:rs_type]
show_non_matching = options[:show_non_matching]

puts "Connecting to db..."
conn = PG.connect(TradeConfig.for mode, :db)
history = HistoryTracker.new conn, rs_type

begin
limits_url = LIMITS_URL_FOR_RS_TYPE[rs_type]
puts "Fetching page at #{limits_url}"
doc = Nokogiri::HTML(open(limits_url))
puts "Fetched."

limits = nil
case rs_type
  when :osrs
    limits = scrape_for_osrs doc
  when :rs3
    limits = scrape_for_rs3 doc
  else
    raise "Unhandled rs_type: #{rs_type}"
end

puts "Got #{limits.size} limit entries."
limits.select!{|k,v| v != 0}
puts "Filtered down to #{limits.size} entries."


updates = []
non_matching = []
has_update=0
puts "Opening transaction..."
conn.transaction do |txn|
  items = ItemTracker.new txn, rs_type
  limits.each do |item, limit|
    matched_ids = nil
    matched_names = nil
    id = items.id_for item
    if id.nil?
      if rs_type == :osrs
        matches = map_non_matching_osrs item
        #puts "Expanded #{item} to #{matches}"
      else
        matches = []
      end

      if matches.empty?
        non_matching << item
        matched_ids = []
        matched_names = []
      else
        matched_ids = matches.map{|name| items.id_for name}
        matched_names = matches
      end
    else
      matched_ids = [id]
      matched_names = [item]
    end
    matched_ids.zip(matched_names).each do |m_id, m_name|
      raise "Special-case for #{m_name} mapped to a null id." if m_id.nil?
      cur_limit = items.limit_for m_id
      next if limit.eql?(cur_limit)
      has_update += 1 
      items.record_limit m_id, limit
      updates << [m_id, cur_limit, limit]
    end
  end

  puts "#{non_matching.size} non-matching items."
  if show_non_matching
    puts "<---->"
    non_matching.each {|i| puts i}
    puts "<---->"
  end
  puts "#{has_update} items w/ new data."
end

history.record_limitmon_result start_ts, limits.size, updates, non_matching.size
puts "Done"

rescue Exception => e
  history.record_exception :limitmon, start_ts, e
  raise e
end
conn.finish
