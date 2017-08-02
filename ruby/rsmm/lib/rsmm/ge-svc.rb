DEFAULT_PORT=46791
require 'socket'
require 'json'

class GEClientRequest
  def initialize client_name, rs_type, hostname="localhost", port=DEFAULT_PORT
    @name = client_name
    @rs_type = rs_type
    @host = hostname
    @port = port
  end

  def query_prices itemid
    s = TCPSocket.new @host, @port
    query = {name: @name, type: "price", item_id: itemid, rs_type: @rs_type}
    s.puts query.to_json
    ret = JSON.parse s.gets
    s.close
    return ret
  end

  def query_category_items category, first_letter, page
    s = TCPSocket.new @host, @port
    query = {name: @name, type: "category", category: category, letter: first_letter, page: page, rs_type: @rs_type}
    s.puts query.to_json
    ret = JSON.parse s.gets
    s.close
    return ret
  end
end
