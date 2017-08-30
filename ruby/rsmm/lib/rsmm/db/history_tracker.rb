require 'date'
class HistoryTracker
  def initialize conn, rs_type
    @conn = conn
    @rs_type = rs_type
  end

  def record_pxmon_result start_ts, n_prices_found
    @conn.transaction do |txn|
      run_id = record_run txn, :pxmon, start_ts, DateTime.now, true, "#{n_prices_found == 0 ? "No" : n_prices_found} prices found."
      txn.exec_params('insert into pxmon_result(run_id, prices_found) values ($1, $2)', [run_id, n_prices_found])
    end
  end

  def record_itemdl_result start_ts, start_ct, end_ct, items_processed
    @conn.transaction do |txn|
      run_id = record_run txn, :itemdl, start_ts, DateTime.now, true, "#{items_processed} items processed."
      txn.exec_params('insert into itemdl_result(run_id, start_count, end_count, items_processed) values ($1, $2, $3, $4)', [run_id, start_ct, end_ct, items_processed])
    end
  end

  def record_limitmon_result start_ts, items_scraped, updated_items, n_non_matching_items
    @conn.transaction do |txn|
      run_id = record_run txn, :limitmon, start_ts, DateTime.now, true, "#{updated_items.size} updates."
      txn.exec_params('insert into limitmon_result(run_id, items_scraped, updated_items, non_matching_items) values($1, $2, $3, $4)',  [run_id, items_scraped, updated_items.size, n_non_matching_items])
      updated_items.each do |item_id, old, new|
        txn.exec_params('insert into limitmon_result_item(run_id, item_id, old_limit, new_limit) values ($1, $2, $3, $4)',  [run_id, item_id, old, new])
      end
    end
  end

  def record_exception process, start_ts, ex
    @conn.transaction do |txn|
      run_id = record_run txn, process, start_ts, DateTime.now, false, "#{ex.class.name}: #{ex.message}"
      txn.exec_params('insert into process_exception(run_id, error_type, message, stack_trace) values ($1, $2, $3, $4)', [run_id, ex.class.name, ex.message, ex.backtrace.join("\n")])
    end
  end

  private
  def record_run txn, process, start_ts, end_ts, success, summary
    run_id_res = txn.exec_params('insert into process_result(run_id, process, rs_type, duration, success, summary) values (DEFAULT, $1, $2, tsrange($3, $4, \'[]\'), $5, $6) returning run_id', [process, @rs_type, start_ts, end_ts, success, summary])
    run_id = run_id_res[0]["run_id"]
    return run_id
  end
end
