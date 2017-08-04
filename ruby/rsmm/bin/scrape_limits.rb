#!/usr/bin/env ruby

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

options = Opts4J4R::parse do |opts|
  opts.sym! :mode, "One of [prod, dev]."
  opts.sym! :rs_type, "One of [osrs, rs3]."
end

mode = options[:mode]
rs_type = options[:rs_type]

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

puts "Connecting to db..."
conn = PG.connect(TradeConfig.for mode, :db)

updates = []
no_match=0
has_update=0
puts "Opening transaction..."
conn.transaction do |txn|
  items = ItemTracker.new txn, rs_type
  limits.each do |item, limit|
    id = items.id_for item
    no_match += 1 if id.nil?
    next if id.nil?
    cur_limit = items.limit_for id
    next if limit.eql?(cur_limit)
    has_update += 1 
    items.record_limit id, limit
    updates << [id, item, cur_limit, limit]
  end
  puts

  puts "#{no_match} non-matching items."
  puts "#{has_update} items w/ new data."
end

update_table = Table.new ["ID", "Item", "Old", "New"], updates

mail_body = "<p>Non-matching items: #{no_match}</p>"
mail_body += "<p>Items with updates: #{has_update}</p><br />"
mail_body += update_table.to_html

mail = Mail.new do
  from     "limitmon@#{TradeConfig.for mode, :mail, :sender_host}"
  to       TradeConfig.for(mode, :mail, :recipients)
  subject  "GE Limit Update Report for #{Date.today.strftime("%d/%m/%Y")}"
  html_part do
    content_type 'text/html; charset=UTF-8'
    body mail_body
  end
end

mail.delivery_method :smtp, address: TradeConfig.for(mode, :mail, :mail_host)
mail.deliver!

puts "Done"
