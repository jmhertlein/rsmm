require 'json'
require 'rsmm/jamflex'
class GERequest 
  TYPE_TO_PATH={osrs: "http://services.runescape.com/m=itemdb_oldschool", rs3: "http://services.runescape.com/m=itemdb_rs"}
  CATEGORY_URL_FORMAT="%s/api/catalogue/items.json?category=%d&alpha=%s&page=%d"
  PRICE_URL_FORMAT="%s/api/graph/%d.json"
  attr_accessor :name, :socket, :details, :type

  def initialize socket, raw
    @socket = socket
    @name = raw["name"]
    @type = raw["type"].to_sym
    @rs_type = raw["rs_type"].to_sym
    @details = raw
  end

  def url
    if @type == :category
      return CATEGORY_URL_FORMAT % [TYPE_TO_PATH[@rs_type], @details["category"], @details["letter"], @details["page"]]
    elsif @type == :price
      return PRICE_URL_FORMAT % [TYPE_TO_PATH[@rs_type], @details["item_id"]]
    end
  end

  def handle_results json
    if @type == :price
      prices = {}
      json["daily"].each do |k,v|
        prices[Jamflex::jamflex_to_date(k)] = v.to_i
      end
      @socket.puts prices.to_json
    else
      items = json["items"].map {|i| {name: i["name"], id: i["id"]} }
      @socket.puts items.to_json
    end
    @socket.close
  end
end
