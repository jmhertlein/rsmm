#!/usr/bin/env ruby

require 'yaml'
require "json"
require "net/http"
require "uri"
require 'mail'
require 'pg'
require 'rsmm/scrape/html/table'
require 'rsmm/config'
require 'rsmm/db'
require 'rsmm/ge-svc'
require 'optparse'

options = Opts4J4R::parse do |opts|
  opts.flag :email, "Send an email on finish or not.", true
  opts.flag :write, "Write results to db. Default true.", true
  opts.sym! :mode, "One of [prod, dev]."
  opts.flag :backfill, "Try to backfill ~90 days of data."
  opts.sym! :rs_type, "One of [osrs, rs3]"
end

send_email = options[:email]
write_to_db = options[:write]
mode = options[:mode]
backfill = options[:backfill]
rs_type = options[:rs_type]

puts "Connecting to database..."
conn = PG.connect(TradeConfig.for mode, :db)
items = ItemTracker.new conn
prices = PriceTracker.new conn
targets = TargetTracker.new conn

# price updates
missing = []
found = []

req = GEClientRequest.new "pxmon", rs_type, TradeConfig.for(mode, :ge_svc, :hostname)
targets.get_targeted_items.each do |itemid|
  puts "Checking price data for #{itemid}"
  price_history = req.query_prices itemid

  if backfill
    found_for_cur_item = false
    price_history.each_entry do |date, price|
      if prices.has_price_for? itemid, date
        puts "Already have price for #{itemid} on #{date}"
      else
        prices.record_price itemid, date, price if write_to_db
        puts "Recorded price for #{itemid} on date #{date}"
        found_for_cur_item = true
      end
    end
    found << itemid if found_for_cur_item
  else
    latest_date = price_history.keys.max
    puts "date is #{latest_date.to_s}"
    price = price_history[latest_date]
    if !prices.has_price_for? itemid, latest_date
      prices.record_price itemid, latest_date, price if write_to_db
      puts "Recorded price for #{itemid}"
      found << itemid
    else
      puts "Already have latest price for #{itemid}"
    end
  end
end

if found.empty?
  puts "No new prices found."
  exit(0)
end

msg_body = "New prices found:<br /><br />#{found.join "<br />"}"

if send_email
  mail = Mail.new do
    from     "pxmon@#{TradeConfig.for mode, :mail, :sender_host}"
    to       TradeConfig.for mode, :mail, :recipients
    subject  "Price Update for #{Date.today.strftime("%d/%m/%Y")} Detected"
    html_part do
      content_type 'text/html; charset=UTF-8'
      body msg_body
    end
  end
  mail.delivery_method :smtp, address: TradeConfig.for(mode, :mail, :mail_host)
  mail.deliver!
end

conn.finish
