class TargetTracker
  def initialize conn, rs_type
    @conn = conn
    @rs_type = rs_type
  end

  def get_targeted_items
    res = @conn.exec_params('SELECT item_id FROM item where favorite=true')
    return res.each.map{|tup| tup["item_id"]}
  end
end
