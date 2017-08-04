#!/usr/bin/env ruby

require 'ostruct'
require 'yaml'
require 'json'

require 'rsmm/ge-svc/ge-request'
require 'rsmm/ge-svc/request-processor'

require 'optparse'

Thread.abort_on_exception = true
STDOUT.sync = true

CONFIG_FILE = "/etc/ge-svc/ge-svc.yml"
DEFAULT_PORT = 46791

cli_options = OpenStruct.new
OptionParser.new do |parser|
  parser.on("-p", "--port [PORT]", Integer, "TCP Port to listen on.") do |port|
    cli_options.port = port
  end
end.parse!

file_options = nil
if File.exist? CONFIG_FILE
  file_options = YAML.load_file CONFIG_FILE
else
  file_options = { port: DEFAULT_PORT }
end

port = DEFAULT_PORT
port = file_options[:port] if !file_options[:port].nil?
port = cli_options.port if !cli_options.port.nil?

processor = RequestProcessor.new
processor.start

shutdown = false
server = TCPServer.new port

Signal.trap("INT") { 
  processor.shutdown
  shutdown = true
  server.close
}

Signal.trap("TERM") {
  processor.shutdown
  shutdown = true
  server.close
}

while !shutdown do
  begin
    Thread.start(server.accept) do |client|
      str = client.gets
      raw = JSON.parse str
      processor.submit GERequest.new(client, raw)
    end
  rescue Errno::EBADF => e
    puts "Socket shutdown."
  end
end

puts "Exiting."
