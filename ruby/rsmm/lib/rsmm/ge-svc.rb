DEFAULT_PORT=46791
require 'socket'
require 'json'

class GEClientRequest
  def initialize client_name, hostname="localhost", port=DEFAULT_PORT
    @name = client_name
    @host = hostname
    @port = port
  end

  def query_prices itemid
    s = TCPSocket.new @host, @port
    query = {name: @name, type: "price", item_id: itemid}
    s.puts query.to_json
    ret = JSON.parse s.gets
    s.close
    return ret
  end

  def query_category_items category, first_letter, page
    s = TCPSocket.new @host, @port
    query = {name: @name, type: "category", category: category, letter: first_letter, page: page}
    s.puts query.to_json
    ret = JSON.parse s.gets
    s.close
    return ret
  end
end
