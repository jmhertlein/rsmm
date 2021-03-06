class ProcessController < ApplicationController

  def index
  end

  def process_error
    @rs_type = process_params["rs_type"].to_sym
    @process_name = process_params["process_name"].to_sym
    run_id = process_params["run_id"].to_i

    @run = ProcessResult.select("*").joins("join process_exception pe on process_result.run_id=pe.run_id").where(run_id: run_id)
    not_found if @run.size == 0
    @run = @run.first
    render "process_error"
  end

  def process_summary
    @rs_type = process_params["rs_type"].to_sym
    not_found unless [:osrs, :rs3].include? @rs_type
    @items_tracked = Item.where(rs_type: @rs_type).count("item_id")
    @last_ge_update_ts = Price.where(rs_type: @rs_type).maximum("created_ts")
    @current_ge_date = Price.where(rs_type: @rs_type).maximum("day")
    @limits_tracked = Item.where(rs_type: @rs_type).where("ge_limit is not null").count("item_id")

    render "process_index"
  end

  def process_runs
    @rs_type = process_params["rs_type"].to_sym
    @process_name = process_params["process_name"].to_sym

    not_found unless [:osrs, :rs3].include? @rs_type
    not_found unless [:pxmon, :itemdl, :limitmon].include? @process_name

    table = "#{@process_name}_result"
    projected_cols = ["pr.*"]
    case @process_name
    when :pxmon
      projected_cols.concat ["prices_found"]
    when :itemdl
      projected_cols.concat ["start_count", "end_count", "items_processed"]
    when :limitmon
      projected_cols.concat ["items_scraped", "updated_items", "non_matching_items"]
    end
    @process_results = ProcessResult.find_by_sql ["select #{projected_cols.join(", ")}
                                                  from process_result pr 
                                                  left join #{table} pt on (pr.run_id=pt.run_id)
                                                  where rs_type=? and process=?
                                                  order by lower(pr.duration) desc
                                                  limit 20",
                                                  @rs_type, @process_name]

    render "#{@process_name}_index"
  end

  def process_run
    @rs_type = process_params["rs_type"].to_sym
    @process_name = process_params["process_name"].to_sym
    run_id = process_params["run_id"].to_i

    not_found unless [:osrs, :rs3].include? @rs_type
    not_found unless [:pxmon, :itemdl, :limitmon].include? @process_name

    table = "#{@process_name}_result"
    @run = ProcessResult.joined_to(table).where(run_id: run_id, rs_type: @rs_type, process: @process_name).first

    case @process_name
    when :pxmon
      render "pxmon"
    when :itemdl
      render "itemdl"
    when :limitmon
      @limit_updates = LimitmonResultItem.find_for_run run_id, @rs_type
      render "limitmon"
    end
  end

  private
  def process_params
    params.require(:rs_type)
    params.permit(:rs_type, :process_name, :run_id)
  end
end
