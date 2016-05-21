require 'pg'

class PositionTracker
  def initialize connection
    @conn = connection
  end

  def open_turns
    res = @conn.exec_params('SELECT turn_id FROM Turn WHERE flat=false')
    return res.each.map{|tup| tup["turn_id"].to_i}
  end

  def open_turn_for_item itemid
    res = @conn.exec_params('SELECT turn_id FROM Turn WHERE flat=false AND itemid=$1', [itemid])
    return res.ntuples == 0 ? -1 : res[0]["turn_id"].to_i
  end

  def has_open_turn? itemid
    return open_turn_for_item(itemid) != -1
  end

  def get_position turn_id
    res = @conn.exec_params('SELECT SUM(quantity) AS sum_qty FROM Trade WHERE turn_id=$1', [turn_id])
    return res.ntuples == 0 ? 0 : res[0]["sum_qty"].to_i
  end

  def get_item_id turn_id
    res = @conn.exec_params('SELECT itemid FROM Turn WHERE turn_id=$1', [turn_id])
    return res.ntuples == 0 ? nil : res[0]["itemid"].to_i
  end

  def record_trade turn_id, order_ts, price, qty
    @conn.exec_params('INSERT INTO Trade VALUES($1, $2, $3, $4)', [turn_id, order_ts, price, qty])
  end

  def open_turn itemid
    @conn.exec_params('INSERT INTO Turn(itemid) VALUES($1)', [itemid])
  end

  def close_turn turn_id
    @conn.exec_params('UPDATE Turn SET flat=true WHERE turn_id=$1', [turn_id])
  end
end
