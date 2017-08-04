require 'net/http'
require 'json'
require 'uri'
require 'date'

module Jamflex
  def self.jamflex_to_date epoch_millis
    return Date.parse(Time.at(epoch_millis[0..-4].to_i).utc.to_s)
  end

  def self.get_json path
    #puts "Fetching #{path}"
    begin
      uri = URI.parse(path)

      http = Net::HTTP.new(uri.host, uri.port)
      request = Net::HTTP::Get.new(uri.request_uri)

      response = http.request(request)

      if response.code == "200"
        if response.body.empty?
          return nil
        else
          return JSON.parse(response.body)
        end
      else
        puts "Weird: non-200 response code: #{response.code}"
        return nil
      end
    rescue Exception => e
      puts "Error getting json: #{e.to_s}"
      return nil
    end
  end
end
