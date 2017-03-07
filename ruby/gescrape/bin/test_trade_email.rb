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
  from     "testemail@#{TradeConfig.for :prod, :mail, :sender_host}"
  to       TradeConfig.for(:prod, :mail, :recipients)
  subject  "Test Email Please Ignore"
  html_part do
    content_type 'text/html; charset=UTF-8'
    body "Test email."
  end
end

mail.delivery_method :smtp, address: TradeConfig.for(:prod, :mail, :mail_host)
mail.deliver!

puts "Done"
