require 'date'
class PriceTracker
  def initialize conn, rs_type
    @conn = conn
    @rs_type = rs_type
  end

  def record_price itemid, date, price, created=DateTime.now
    @conn.exec_params('INSERT INTO Price(item_id, day, rs_type, price, created_ts) VALUES($1, $2, $3, $4, $5)', [itemid, date, @rs_type, price, created])
  end

  def price_for itemid, date=Date.today
    res = @conn.exec_params('SELECT price FROM Price WHERE item_id=$1 AND day=$2 and rs_type=$3', [itemid,date,@rs_type])

    return res.ntuples == 0 ? nil : res[0]["price"].to_i
  end

  def has_price_for? itemid, date=Date.today
    return !price_for(itemid, date).nil?
  end

  def latest_price_for itemid
    res = @conn.exec_params('SELECT price FROM Price WHERE item_id = $1 AND rs_type=$2 AND day=(SELECT MAX(day) FROM Price WHERE item_id=$1 and rs_type=$2)', [itemid, @rs_type])
    if res.ntuples == 0
      raise "missing latest price for #{itemid}"
    end
    return res[0]["price"].to_i
  end

  def notional_value_of itemid, qty
    return latest_price_for(itemid) * qty
  end

  def prices_for itemid, date=Date.today, n_days=180
    res = @conn.exec_params('SELECT day,price FROM Price WHERE item_id = $1 AND rs_type=$4 AND day BETWEEN $3 AND $2 ORDER BY day ASC', [itemid, date, date-(n_days-1), @rs_type])
    raise "not enough price data (found #{res.ntuples}, needed #{n_days})" if res.ntuples != n_days
    return res.each.map{|tup| tup["price"].to_i}
  end

  def avg_ft itemid, n_days
    prices = prices_for itemid, Date.today, n_days
    followthrus = []
    cur = 0
    last_px = prices[0]
    prices[1..-1].each do |px|
      if px > last_px
        cur += 1
      elsif px == last_px
      else
        followthrus << cur if cur > 1
        cur = 0
      end
      last_px = px
    end
    
    return followthrus.reduce(:+).to_f / followthrus.size.to_f
  end
end
