class ProcessController < ApplicationController
  def index
    @items_tracked = Price.connection.select_all("select count(item_id) from item where rs_type='osrs';")[0]["count"].to_s
    @last_ge_update_ts = Time.parse(Price.connection.select_all("select coalesce(max(created_ts), '1970-01-01'::timestamp) as last_update from price where rs_type='osrs';")[0]["last_update"]).strftime("%T")
    @current_ge_date = Price.connection.select_all("select coalesce(max(day), '1970-01-01'::timestamp) from price where rs_type='osrs'")[0]["coalesce"].to_s
    @limits_tracked = Price.connection.select_all("select count(item_id) from item where ge_limit is not null and rs_type='osrs'")[0]["count"].to_s
  end
end
