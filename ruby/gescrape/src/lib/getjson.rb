require 'net/http'
require 'json'
require 'uri'

def get_json path
  puts "Fetching #{path}"
  uri = URI.parse(path)

  http = Net::HTTP.new(uri.host, uri.port)
  request = Net::HTTP::Get.new(uri.request_uri)

  response = http.request(request)

  if response.code == "200"
    return JSON.parse(response.body)
  else
    raise "error getting json"
  end
end
