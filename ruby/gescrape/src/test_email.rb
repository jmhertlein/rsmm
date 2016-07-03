#!/usr/bin/env ruby

require 'yaml'
require "json"
require "net/http"
require "uri"
require 'open-uri'
require 'pg'
require 'nokogiri'
require 'mail'
require_relative 'lib/item_tracker.rb'
require_relative 'lib/html/table.rb'
require_relative '../config/db.rb'
require_relative '../config/mail.rb'

mail = Mail.new do
  from     "testemail@#{MAIL_INFO[:sender_host]}"
  to       MAIL_INFO[:recipients]
  subject  "Test Email Please Ignore"
  html_part do
    content_type 'text/html; charset=UTF-8'
    body "Test email."
  end
end

mail.delivery_method :smtp, address: MAIL_INFO[:mail_host]
mail.deliver!

puts "Done"
