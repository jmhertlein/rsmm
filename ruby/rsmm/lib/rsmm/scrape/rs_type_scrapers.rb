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
