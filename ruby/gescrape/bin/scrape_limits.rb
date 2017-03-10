#!/usr/bin/env ruby

require 'optparse'
require 'yaml'
require "json"
require "net/http"
require "uri"
require 'open-uri'
require 'pg'
require 'nokogiri'
require 'mail'
require 'gescrape/html/table'
require 'trade-db'
require 'trade-config'

GE_LIMITS_URL="http://runescape.wikia.com/wiki/Ge_limits"

mode = :prod
OptionParser.new do |opts|
  opts.banner = "Usage: "

  opts.on("-mMODE", "--mode=MODE", "Pick mode. Default prod.") do |m|
    mode = m.to_sym
  end
end.parse!

puts "Fetching page at #{GE_LIMITS_URL}"
doc = Nokogiri::HTML(open(GE_LIMITS_URL))
puts "Fetched."

limits = Hash.new
doc.search("table").each do |table|
  headers = table.search("th")
  puts table
  next if headers[0].nil? || !headers[0].text.strip.eql?("Item") || !headers[1].text.strip.eql?("Limit")

  table.search("tr").each do |row|
    items = row.search("td")
    next if items.empty?
    item_name = items[0].text.strip
    item_limit = items[1].text.strip.gsub(/,/, "").to_i
    limits[item_name] = item_limit
  end
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
  items = ItemTracker.new txn
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
