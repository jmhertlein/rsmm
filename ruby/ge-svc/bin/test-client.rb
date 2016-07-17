#!/usr/bin/env ruby

require 'optparse'
require 'ostruct'
require 'yaml'
require 'json'
require 'socket'

DEFAULT_PORT = 46791

puts "i am #{ARGV[0]}"
req = {name: ARGV[0], type: "category", category: 1, letter: "a", page: 1}

sockets = []

(1..10).each do |pg|
  req[:page] = pg
  socket = TCPSocket.new "localhost", DEFAULT_PORT
  socket.puts req.to_json
  sockets << socket
end
puts "wrote 10"

sockets.each do |s|
  s.gets
  puts "got response."
  s.close
end
