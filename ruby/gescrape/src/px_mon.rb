#!/usr/bin/env ruby

require 'yaml'
require "json"
require "net/http"
require "uri"
require 'mail'
require 'pg'
require_relative 'lib/html/table.rb'
require_relative 'lib/ge/ge_api.rb'
require_relative 'lib/price_tracker.rb'
require_relative 'lib/item_tracker.rb'
require_relative 'lib/target_tracker.rb'
require_relative '../config/db.rb'
require_relative '../config/mail.rb'

puts "Connecting to database..."
conn = PG.connect(DB_INFO)
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
  from     'pxmon@jmhertlein.net'
  to       MAIL_INFO[:recipients]
  subject  "Price Update for #{Date.today.strftime("%d/%m/%Y")} Detected"
  body msg_body
end
mail.delivery_method :smtp, address: MAIL_INFO[:mail_host]
mail.deliver!

conn.finish
