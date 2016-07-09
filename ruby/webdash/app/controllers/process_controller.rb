class ProcessController < ApplicationController
  def index
    @items_tracked = Price.connection.select_all("select count(item_id) from item;")[0]["count"].to_s
    @last_ge_update_ts = Time.parse(Price.connection.select_all("select max(created_ts) from price;")[0]["max"]).strftime("%T")
    @current_ge_date = Price.connection.select_all("select max(day) from price")[0]["max"].to_s
    @limits_tracked = Price.connection.select_all("select count(item_id) from item where ge_limit is not null")[0]["count"].to_s
  end
end