require 'json'
class GERequest 
  CATEGORY_URL_FORMAT="http://services.runescape.com/m=itemdb_rs/api/catalogue/items.json?category=%d&alpha=%s&page=%d"
  PRICE_URL_FORMAT="http://services.runescape.com/m=itemdb_rs/api/graph/%d.json"
  attr_accessor :name, :socket, :details, :type

  def initialize socket, raw
    @socket = socket
    @name = raw["name"]
    @type = raw["type"].to_sym
    @details = raw
  end

  def url
    if @type == :category
      return CATEGORY_URL_FORMAT % [@details["category"], @details["letter"], @details["page"]]
    elsif @type == :price
      return PRICE_URL_FORMAT % [@details["item_id"]]
    end
  end

  def handle_results json
    if @type == :price
      prices = {}
      json["daily"].each do |k,v|
        prices[jamflex_to_date(k)] = v.to_i
      end
      @socket.puts prices.to_json
    else
      items = json["items"].map {|i| {name: i["name"], id: i["id"]} }
      @socket.puts items.to_json
    end
    @socket.close
  end
end
