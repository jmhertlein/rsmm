class ProfitTracker
  def initialize conn, prices, position
    @conn = conn
    @prices = prices
    @position = position
  end

  def open_profit_for turn_id
    res = @conn.exec_params(
'SELECT turn_id, itemid, SUM(price * quantity) AS total_cost, SUM(quantity) AS entry_qty
FROM Trade NATURAL JOIN Turn
WHERE flat=false AND quantity > 0 AND turn_id=$1
GROUP BY turn_id,itemid', [turn_id])

    return res.ntuples == 0 ? nil : open_profit_for_tuple(res[0])
  end

  def open_profit
    res = @conn.exec_params(
'SELECT turn_id, itemid, SUM(price * quantity) AS total_cost, SUM(quantity) AS entry_qty
FROM Trade NATURAL JOIN Turn
WHERE flat=false AND quantity > 0
GROUP BY turn_id,itemid
')
    open_profit = 0
    res.each do |tuple| 
      open_profit += open_profit_for_tuple(tuple)
    end

    return open_profit
  end

  def open_profit_for_tuple tuple
    held_qty = @position.get_position(tuple["turn_id"].to_i)
    avg_entry_px = tuple["total_cost"].to_i / tuple["entry_qty"].to_i
    return (@prices.notional_value_of(tuple["itemid"].to_i, held_qty) - (avg_entry_px * held_qty))
  end

  private :open_profit_for_tuple
end
