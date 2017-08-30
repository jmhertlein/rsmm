#!/usr/bin/env ruby

require 'date'
require 'yaml'
require "json"
require "net/http"
require "uri"
require 'pg'
require 'rsmm/db'
require 'rsmm/config'
require 'rsmm/ge-svc'
require 'opts4j4r'

TYPE_TO_VALID_CATEGORIES={osrs: (1..1), rs3: (0..37)}

start_ts = DateTime.now

send_email = true
mode = :prod
options = Opts4J4R::parse do |opts|
  #opts.flag :write, "Write results to db. Default true."
  opts.sym! :mode, "One of [prod, dev]."
  opts.sym! :rs_type, "One of [osrs, rs3]"
end

mode = options[:mode]
rs_type = options[:rs_type]

puts "Connecting to db..."
conn = PG.connect(TradeConfig.for mode, :db)
history = HistoryTracker.new conn, rs_type
begin

initial_count = -1
final_count = -1
total_processed = 0

req = GEClientRequest.new "itemdb", rs_type, TradeConfig.for(mode, :ge_svc, :hostname)

found_items = []
TYPE_TO_VALID_CATEGORIES[rs_type].each do |ctg|
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
        page.each {|item| found_items << item}
      end
      page_no += 1
    end
  end
end

puts "Opening transaction..."
conn.transaction do |txn|
  items = ItemTracker.new txn, rs_type
  
  initial_count = items.count_all
  puts "Count at start: #{initial_count}"

  found_items.each {|item| items.record_id(item["id"], item["name"])}

  final_count = items.count_all
  puts "Final count: #{final_count}"
  puts "Total Processed: #{total_processed}"
end

history.record_itemdl_result start_ts, initial_count, final_count, found_items.size
rescue Exception => e
  history.record_exception :itemdl, start_ts, e
  raise e
end

puts "Done"
