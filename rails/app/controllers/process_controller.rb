class ProcessController < ApplicationController

  def index
    
  end

  def process_summary
    rs_type = process_params["rs_type"].to_sym
    not_found unless [:osrs, :rs3].include? rs_type
    @items_tracked = Item.where(rs_type: rs_type).count("item_id")
    @last_ge_update_ts = Price.where(rs_type: rs_type).maximum("created_ts")
    @current_ge_date = Price.where(rs_type: rs_type).maximum("day")
    @limits_tracked = Item.where(rs_type: rs_type).where("ge_limit is not null").count("item_id")

    render "index"
  end

  def process_runs
    rs_type = process_params["rs_type"].to_sym
    process_name = process_params["process_name"].to_sym

    not_found unless [:osrs, :rs3].include? rs_type
    not_found unless [:pxmon, :itemdb, :limitmon].include? process_name

    table = "#{process_name}_result"

    @process_results = ProcessResult.joined_to(table).where(rs_type: rs_type, process: process_name)

    render "#{process_name}_index"
  end

  def process_run
    rs_type = process_params["rs_type"].to_sym
    process_name = process_params["process_name"].to_sym
    run_id = process_params["run_id"].to_i
    
    not_found unless [:osrs, :rs3].include? rs_type
    not_found unless [:pxmon, :itemdb, :limitmon].include? process_name

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
    params.permit(:rs_type, :process_name, :run_id)
  end

  private
  def query sql, binds
    Price.connection.select_all(sql, "", binds.map{|b| [nil, b]})
  end
end
