#!/usr/bin/env ruby

require 'yaml'
require "json"
require "net/http"
require "uri"
require 'mail'
require 'pg'
require 'rsmm/db'
require 'rsmm/config'
require 'rsmm/ge-svc'
require 'opts4j4r'

send_email = true
mode = :prod
options = Opts4J4R::parse do |opts|
  opts.flag :email, "Send an email on finish or not.", true
  #opts.flag :write, "Write results to db. Default true."
  opts.sym! :mode, "One of [prod, dev]."
  opts.sym! :rs_type, "One of [osrs, rs3]"
end

mode = options[:mode]
send_email = options[:email]
rs_type = options[:rs_type]

if send_email
  puts "Sending initial email..."
  mail = Mail.new do
    from "itemdb@#{TradeConfig.for mode, :mail, :sender_host}"
    to   TradeConfig.for(mode, :mail, :recipients)
    subject "Item Table Update For #{Date.today.strftime("%d/%m/%Y")}"
    html_part do
      content_type 'text/html; charset=UTF-8'
      body 'Starting update...'
    end
  end
  mail.delivery_method :smtp, address: TradeConfig.for(mode, :mail, :mail_host)
  mail.deliver!
end

puts "Connecting to db..."
conn = PG.connect(TradeConfig.for mode, :db)

initial_count = -1
final_count = -1
total_processed = 0
errored_items = []

req = GEClientRequest.new "itemdb", rs_type, TradeConfig.for(mode, :ge_svc, :hostname)
puts "Opening transaction..."
conn.transaction do |txn|
  items = ItemTracker.new txn, rs_type
  
  initial_count = items.count_all
  puts "Count at start: #{initial_count}"

  (0..37).each do |ctg|
    puts "--------Category: #{ctg}"
    ('a'..'z').each do |let|
      puts "----Letter: #{let}"
      page_no = 1
      empty_resp = false
      while !empty_resp
        puts "--Page: #{page_no}"
        page = req.query_category_items ctg, let, page_no
        if page.empty?
          empty_resp = true
          puts "Hit last page"
        else
          puts "Recording #{page.size} items..."
          total_processed += page.size
          page.each {|item| items.record_id(item["id"], item["name"])}
        end
        page_no += 1
      end
    end
  end

  final_count = items.count_all
  puts "Final count: #{final_count}"
  puts "Total Processed: #{total_processed}"
end

if send_email
  puts "Emailing..."
  mail = Mail.new do
    from "itemdb@#{TradeConfig.for mode, :mail, :sender_host}"
    to   TradeConfig.for(mode, :mail, :recipients)
    subject "Item Table Update For #{Date.today.strftime("%d/%m/%Y")}"
    body "Item update finished.\n\nStart Count: #{initial_count}, End Count: #{final_count}\nItems Processed: #{total_processed}\n\nErrors:#{errored_items.join("\n")}\n\n"
  end
  mail.delivery_method :smtp, address: TradeConfig.for(mode, :mail, :mail_host)
  mail.deliver!
end

puts "Done"
