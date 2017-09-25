class ProcessController < ApplicationController
  def index
    @items_tracked = Price.connection.select_all("select count(item_id) from item where rs_type='osrs';")[0]["count"].to_s
    @last_ge_update_ts = Time.parse(Price.connection.select_all("select coalesce(max(created_ts), '1970-01-01'::timestamp) as last_update from price where rs_type='osrs';")[0]["last_update"]).strftime("%T")
    @current_ge_date = Price.connection.select_all("select coalesce(max(day), '1970-01-01'::timestamp) from price where rs_type='osrs'")[0]["coalesce"].to_s
    @limits_tracked = Price.connection.select_all("select count(item_id) from item where ge_limit is not null and rs_type='osrs'")[0]["count"].to_s
  end

  def process_runs
    rs_type = process_params["rs_type"].to_sym
    process_name = process_params["process_name"].to_sym

    render_404! unless [:osrs, :rs3].include? rs_type
    render_404! unless [:pxmon, :itemdb, :limitmon].include? process_name

    table = "#{process_name}_result"

    @process_results = query("select * from process_result r join pxmon_result pt on (r.run_id=pt.run_id) where rs_type = $1 and process = $2", [rs_type, process_name])

    render "#{process_name}_index"
  end

  def process_run
    rs_type = process_params["rs_type"].to_sym
    process_name = process_params["process_name"].to_sym
    run_id = process_params["run_id"].to_i
    
    render_404! unless [:osrs, :rs3].include? rs_type
    render_404! unless [:pxmon, :itemdb, :limitmon].include? process_name

    @run_row = @process_results = Price.connection.select_all("select * from process_result r join :ptable pt on (r.run_id=pt.run_id) where rs_type=:rs_type and process=:pname and r.run_id=:run_id", {ptable: table, rs_type: rs_type, pname: process_name, run_id: run_id})

    case process_name
    when :pxmon
      render "pxmon"
    when :itemdb
      render "itemdb"
    when :limitmon
      @limit_updates = Price.connection.select_all("select * from limitmon_result_item where run_id=:run_id", {run_id: run_id})
      render "limitmon"
    end
  end


  private
  def process_params
    params.require(:rs_type)
    params.require(:process_name)
    params.permit(:rs_type, :process_name, :run_id)
  end

  private
  def query sql, binds
    Price.connection.select_all(sql, "", binds.map{|b| [nil, b]})
  end
end
