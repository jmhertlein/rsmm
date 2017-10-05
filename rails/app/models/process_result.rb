class ProcessResult< RSMMBase
  self.table_name = "process_result"
  self.primary_key = "run_id"

  def self.joined_to table_name
    joins_clause = ProcessResult.sanitize_sql_array ["", table_name]
    return select("*").joins joins_clause
  end
end
