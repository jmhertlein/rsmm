#!/usr/bin/env ruby

require 'yaml'
require "json"
require "net/http"
require "uri"
require 'open-uri'
require 'pg'
require 'nokogiri'
require 'mail'
require 'gescrape/html/table'
require 'trade-db'
require 'trade-config'

mail = Mail.new do
  from     "testemail@#{TradeConfig::MAIL_INFO[:sender_host]}"
  to       TradeConfig::MAIL_INFO[:recipients]
  subject  "Test Email Please Ignore"
  html_part do
    content_type 'text/html; charset=UTF-8'
    body "Test email."
  end
end

mail.delivery_method :smtp, address: TradeConfig::MAIL_INFO[:mail_host]
mail.deliver!

puts "Done"