class LimitmonResultItem < RSMMBase
  self.table_name = "limitmon_result_item"

  def self.find_for_run run_id, rs_type
    joins_clause = LimitmonResultItem.sanitize_sql_array ["join item on (limitmon_result_item.item_id=item.item_id and item.rs_type=?)", rs_type]
    return select("*").joins(joins_clause).where(run_id: run_id)
  end
end
