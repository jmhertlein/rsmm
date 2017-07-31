include ActionView::Helpers::NumberHelper

class TradingController < ApplicationController
  helper_method :commas, :gp_fmt
  def index
    @total_profit = Price.connection.select_all("SELECT SUM(price * (quantity*-1)) AS total_closed FROM Trade NATURAL JOIN Turn WHERE close_ts IS NOT NULL;")[0]["total_closed"]
    @total_volume = Price.connection.select_all("select sum(abs(quantity)) from trade")[0]["sum"]
    @total_notional = Price.connection.select_all("select coalesce(sum(abs(quantity)*price)/2,0) from trade")[0]["coalesce"]
    
    @daily_stats = Price.connection.select_all("select * from daily_history order by day desc limit 5")

    raw_long_term_stats = Price.connection.select_all("select day,closed_profit,volume from daily_history order by day")
    raw_long_term_stats.inspect
    @long_term_profit = running_sum(raw_long_term_stats.rows.map{|row| [Date.parse(row[0]), row[1].to_i] })
    @long_term_volume = running_sum(raw_long_term_stats.rows.map{|row| [Date.parse(row[0]), row[2].to_i]})
    @long_term_ppt = Price.connection.select_all("select day, avg(sum) from (select max(open_ts::date) as day, turn_id, sum(price*quantity*-1) from trade natural join turn group by turn_id) as A group by day;").rows.map{|row| [Date.parse(row[0]), row[1].to_i]}
  end

  def commas s
    return number_with_delimiter(s, :delimiter => ',')
  end

  def gp_fmt s
    gp = s.to_i
    if gp < 100000
      return number_with_delimiter(gp, :delimiter => ',')
    elsif gp < 10000000
      return "#{number_with_delimiter(gp/1000, :delimiter => ',')}k"
    else
      return "#{number_with_delimiter(gp/1000000, :delimiter => ',')}M"
    end
  end

  def running_sum profit_series
    sum = 0
    return profit_series.map{|row| [row[0], sum += row[1]]}
  end
end
