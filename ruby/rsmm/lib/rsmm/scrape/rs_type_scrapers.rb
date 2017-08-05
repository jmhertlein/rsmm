def scrape_for_rs3 doc
  limits = Hash.new
  doc.search("table").each do |table|
    headers = table.search("th")
    next if headers[0].nil? || !headers[0].text.strip.eql?("Item") || !headers[1].text.strip.eql?("Limit")

    table.search("tr").each do |row|
      items = row.search("td")
      next if items.empty?
      item_name = items[0].text.strip
      item_limit = items[1].text.strip.gsub(/,/, "").to_i
      limits[item_name] = item_limit
    end
  end
  return limits
end

def scrape_for_osrs doc
  limits = Hash.new
  doc.search("table").each do |table|
    headers = table.search("th")
    next if headers[0].nil? || !headers[0].text.strip.eql?("Item Name") || !headers[1].text.strip.include?("Item Limit")

    table.search("tr").each do |row|
      items = row.search("td")
      next if items.empty?
      item_name = items[0].text.strip
      item_limit = items[1].text.strip.gsub(/,/, "").to_i
      limits[item_name] = item_limit
    end
  end
  return limits
end

def to_doses name
  [1..4].map{|dose| "#{name}(#{dose})"}

def map_non_matching_osrs name
  case name
    when "Antidote++": to_doses name
    when name.match?(/Super (attack|strength|defense|restore)/): to_doses name
    when name.match?(/[A-Z][a-z]+\spotion/): to_doses name
    when "Saradomin brew": to_doses name
    when "Zamorak brew": to_doses name
    else []
  end
end
