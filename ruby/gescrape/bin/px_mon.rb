#!/usr/bin/env ruby

require 'yaml'
require "json"
require "net/http"
require "uri"
require 'mail'
require 'pg'
require 'ge-api'
require 'gescrape/html/table'
require 'trade-config'
require 'trade-db'

puts "Connecting to database..."
conn = PG.connect(TradeConfig::DB_INFO)
items = ItemTracker.new conn
prices = PriceTracker.new conn
targets = TargetTracker.new conn

# price updates
missing = []
found = []
targets.get_targeted_items.each do |itemid|
  puts "Checking price data for #{itemid}"
  if !prices.has_price_for? itemid
    latest_date, price = dl_latest_price_for_item(itemid)
    if latest_date.eql?(Date.today)
      prices.record_price itemid, Date.today, price
      puts "Recorded price for #{itemid}"
      found << itemid
    else
      puts "No price for today available for #{itemid} yet."
      missing << itemid
    end
  else
    puts "Already have today's price for #{itemid}"
  end
end

if found.empty?
  puts "No new prices found."
  exit(0)
end

msg_body = "New prices found:\n\n#{found.join "\n"}"

mail = Mail.new do
  from     "pxmon@#{TradeConfig::MAIL_INFO[:sender_host]}"
  to       TradeConfig::MAIL_INFO[:recipients]
  subject  "Price Update for #{Date.today.strftime("%d/%m/%Y")} Detected"
  html_part do
    content_type 'text/html; charset=UTF-8'
    body msg_body
  end
end
mail.delivery_method :smtp, address: TradeConfig::MAIL_INFO[:mail_host]
mail.deliver!

conn.finish
