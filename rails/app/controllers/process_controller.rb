class ProcessController < ApplicationController
  def index
    @items_tracked = Price.connection.select_all("select count(item_id) from item where rs_type='osrs';")[0]["count"].to_s
    @last_ge_update_ts = Time.parse(Price.connection.select_all("select coalesce(max(created_ts), '1970-01-01'::timestamp) as last_update from price where rs_type='osrs';")[0]["last_update"]).strftime("%T")
    @current_ge_date = Price.connection.select_all("select coalesce(max(day), '1970-01-01'::timestamp) from price where rs_type='osrs'")[0]["coalesce"].to_s
    @limits_tracked = Price.connection.select_all("select count(item_id) from item where ge_limit is not null and rs_type='osrs'")[0]["count"].to_s
  end

  def process_runs
    rs_type = process_params["rs_type"]
    render_404! unless [:osrs, :rs3].include? rs_type

    process_name = process_params["process_name"].to_sym
    table = nil
    case process_name
    when :pxmon
      table = "pxmon_result"
    when :itemdb
      table = "itemdb_result"
    when :limitmon
      table = "limitmon_result"
    else
      render_404!
    end

    @process_results = Price.connection.select_all("select * from process_result r join :ptable pt on (r.run_id=pt.run_id) where rs_type=:rs_type and process=:pname", {ptable: table, rs_type: rs_type, pname: process_name})
  end

  def process_run
    process_name = process_params["process_name"].to_sym
    run_id = process_params["run_id"]

  end


  private
  def process_params
    params.require(:rs_type).require(:process_name).permit(:run_id)
  end
end
