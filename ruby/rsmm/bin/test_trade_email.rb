#!/usr/bin/env ruby

require 'yaml'
require "json"
require "net/http"
require "uri"
require 'open-uri'
require 'pg'
require 'nokogiri'
require 'mail'
require 'optparse'
require 'rsmm/scrape/html/table'
require 'rsmm/db'
require 'rsmm/config'

mode = :prod
OptionParser.new do |opts|
  opts.banner = "Usage: "

  opts.on("-mMODE", "--mode=MODE", "Pick mode. Default prod.") do |m|
    mode = m.to_sym
  end
end.parse!

mail = Mail.new do
  from     "testemail@#{TradeConfig.for mode, :mail, :sender_host}"
  to       TradeConfig.for(mode, :mail, :recipients)
  subject  "Test Email Please Ignore"
  html_part do
    content_type 'text/html; charset=UTF-8'
    body "Test email."
  end
end

mail.delivery_method :smtp, address: TradeConfig.for(mode, :mail, :mail_host)
mail.deliver!

puts "Done"
