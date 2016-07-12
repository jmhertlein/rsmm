require_relative 'ge/ge_api.rb'

def slurp_prices_for prices, itemid
  dl = dl_prices_for_item(itemid)

  puts "Got #{dl.size} prices."
  added = 0
  dl.each_pair do |millis,px|
    begin
      prices.record_price itemid, jamflex_to_date(millis), px
      added += 1
    rescue
      puts "Skipping #{jamflex_to_date(millis).to_s}"
    end
  end

  puts "Added #{added} prices."
end
