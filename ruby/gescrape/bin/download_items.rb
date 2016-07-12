#!/usr/bin/env ruby

require 'yaml'
require "json"
require "net/http"
require "uri"
require 'mail'
require 'pg'
require 'ge-api'
require 'trade-db'
require 'trade-config'

puts "Sending initial email..."
mail = Mail.new do
  from "itemdb@#{TradeConfig::MAIL_INFO[:sender_host]}"
  to   TradeConfig::MAIL_INFO[:recipients]
  subject "Item Table Update For #{Date.today.strftime("%d/%m/%Y")}"
  html_part do
    content_type 'text/html; charset=UTF-8'
    body 'Starting update...'
  end
end
mail.delivery_method :smtp, address: TradeConfig::MAIL_INFO[:mail_host]
mail.deliver!

puts "Connecting to db..."
conn = PG.connect(TradeCondig::DB_INFO)

initial_count = -1
final_count = -1
total_processed = 0
errored_items = []

puts "Opening transaction..."
conn.transaction do |txn|
  items = ItemTracker.new txn
  
  initial_count = items.count_all
  puts "Count at start: #{initial_count}"

  (0..37).each do |ctg|
    puts "--------Category: #{ctg}"
    ('a'..'z').each do |let|
      puts "----Letter: #{let}"
      page_no = 1
      empty_resp = false
      attempts = 0
      while attempts < 5 && !empty_resp
        begin
          puts "--Page: #{page_no} (attempt #{attempts})"
          url="http://services.runescape.com/m=itemdb_rs/api/catalogue/items.json?category=#{ctg}&alpha=#{let}&page=#{page_no}"
          sleep 6
          page = get_json url
          if page["items"].empty?
            empty_resp = true
            puts "Hit last page"
          else
            puts "Recording #{page["items"].size} items..."
            total_processed += page["items"].size
            page["items"].each {|item| items.record_id(item["id"], item["name"])}
          end
          page_no += 1
        rescue Exception => e
          attempts += 1
          puts "ERROR: #{e.to_s}, RETRYING in 5s"
          sleep 5
        end
      end
      if attempts >= 5
        errored_items << "CTG: #{ctg}, LETTER: #{let}, PAGE: #{page_no}"
      end
    end
  end

  final_count = items.count_all
  puts "Final count: #{final_count}"
  puts "Total Processed: #{total_processed}"
end

puts "Emailing..."
mail = Mail.new do
  from "itemdb@#{TradeConfig::MAIL_INFO[:sender_host]}"
  to   TradeConfig::MAIL_INFO[:recipients]
  subject "Item Table Update For #{Date.today.strftime("%d/%m/%Y")}"
  body "Item update finished.\n\nStart Count: #{initial_count}, End Count: #{final_count}\nItems Processed: #{total_processed}\n\nErrors:#{errored_items.join("\n")}\n\n"
end
mail.delivery_method :smtp, address: TradeConfig::MAIL_INFO[:mail_host]
mail.deliver!

puts "Done"
