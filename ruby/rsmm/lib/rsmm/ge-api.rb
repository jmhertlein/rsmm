require 'net/http'
require 'json'
require 'uri'

class GE
  OSRS_BASE_URL = "http://services.runescape.com/m=itemdb_oldschool"
  RS3_BASE_URL = "http://services.runescape.com/m=itemdb_rs"
  attr_reader :rs_type
  def initialize rs_type
    @rs_type = rs_type
    case @rs_type
      when :osrs
        @base_path = OSRS_BASE_URL
      when :rs3
        @base_path = RS3_BASE_URL
      else
        raise "Unhandled rs_type: #{@rs_type}"
    end
  end

  def get_json_for_item itemid
      return get_json("#{@base_path}/api/graph/#{itemid}.json")
  end

  def dl_prices_for_item itemid
    json = get_json_for_item itemid
    return json["daily"]
  end

  def jamflex_to_date epoch_millis
    return Date.parse(Time.at(epoch_millis[0..-4].to_i).utc.to_s)
  end

  def dl_latest_price_for_item itemid
    prices = dl_prices_for_item(itemid)
    latest_date = prices.keys.sort.last
    return jamflex_to_date(latest_date), prices[latest_date]
  end

  def get_json path
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
