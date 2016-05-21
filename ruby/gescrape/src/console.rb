#!/usr/bin/env ruby

require_relative 'lib/position_tracker.rb'
require_relative 'lib/slurp_prices.rb'
require_relative 'lib/item_tracker.rb'
require_relative 'lib/price_tracker.rb'
require_relative 'lib/profit_tracker.rb'
require_relative 'lib/target_tracker.rb'
require_relative '../config/db.rb'
require 'pg'
require 'colorize'

def trade itemname, qty, px, position, items
  itemname.gsub!(/_/, ' ')
  id = items.id_for itemname
  if id.nil?
    puts "No such item: #{itemname}"
    return
  end

  if qty == 0
    puts "Zero-quantity order not allowed."
    return
  end

  position.open_turn(id) if !position.has_open_turn? id
  turn_id = position.open_turn_for_item id
  position.record_trade turn_id, Time.now, px, qty
  puts "Trade recorded: #{itemname} (#{id}) x#{qty} @ #{px}gp each."

  puts "New position is #{position.get_position(turn_id)}"
  
  if position.get_position(turn_id).to_i == 0
    position.close_turn turn_id
    puts "Closed turn."
  end
end

def list_open_turns pos, items, profit
  turns = pos.open_turns
  puts "Item\t\t\tPosition\t\t\tOpen Profit"
  turns.each do |turn_id|
    itemid = pos.get_item_id(turn_id)
    itemname = items.name_for itemid
    open_profit = profit.open_profit_for(turn_id)
    puts "#{itemname}\t\t\t#{pos.get_position turn_id}\t\t\t#{open_profit}"
  end
end

def add_new_target name, qty, conn, items
  conn.exec_params('INSERT INTO OrderTemplate VALUES($1, $2)', [items.id_for(name), qty])
  puts "Now targeting #{name} (#{items.id_for name})."
end

puts "#######################"
puts "## Trading Dashboard ##"
puts "#######################"

conn = PG.connect(DB_INFO)
position = PositionTracker.new conn
items = ItemTracker.new conn
prices = PriceTracker.new conn
targets = TargetTracker.new conn
profit = ProfitTracker.new conn, prices, position

print ">"
raw = gets
while !raw.nil? && raw != "exit"
  cmd = raw.chomp.split(' ')
  case cmd[0]
    when "buy"
      itemname = cmd[2]
      qty = cmd[1].to_i
      px = cmd[3].to_i
      trade itemname, qty, px, position, items
    when "sell"
      itemname = cmd[2]
      qty = cmd[1].to_i * -1
      px = cmd[3].to_i
      trade itemname, qty, px, position, items
    when "open"
      list_open_turns position, items, profit
    when "target"
      add_new_target cmd[1].gsub(/_/, ' '), cmd[2].to_i, conn, items
    when "targets"
      puts "Targets:"
      puts targets.get_targeted_items.map{|id| items.name_for id}.join("\n")
    when "limit"
      limit = cmd[1]
      puts "NOP"
    when "slurp"
      itemid = items.id_for(cmd[1].gsub(/_/, ' '))
      slurp_prices_for prices, itemid
    when "slurpall"
      targets.get_targeted_items.each {|id| slurp_prices_for(prices, id)}
      puts "Done slurping."
    else
      puts "[buy|sell] [name] [qty] [px]"
      puts "target [name] [qty]"
      puts "targets"
      puts "slurp [item]"
      puts "slurpall"
      puts "open"
  end
  print ">"
  raw = gets
end

conn.finish
