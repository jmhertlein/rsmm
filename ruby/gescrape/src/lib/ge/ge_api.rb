require_relative '../getjson.rb'

def get_json_for_item itemid
    return get_json("http://services.runescape.com/m=itemdb_rs/api/graph/#{itemid}.json")
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
