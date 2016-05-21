class TargetTracker
  def initialize conn
    @conn = conn
  end

  def get_targeted_items
    res = @conn.exec_params('SELECT itemid FROM OrderTemplate')
    return res.each.map{|tup| tup["itemid"]}
  end

  def get_quantity_for itemid
    res = @conn.exec_params('SELECT quantity FROM OrderTemplate WHERE itemid=$1', [itemid])
    return res.ntuples == 0 ? nil : res["quantity"]
  end
end
