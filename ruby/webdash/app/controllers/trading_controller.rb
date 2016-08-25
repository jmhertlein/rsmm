include ActionView::Helpers::NumberHelper

class TradingController < ApplicationController
  helper_method :commas, :gp_fmt
  def index
    @total_profit = Price.connection.select_all("SELECT SUM(price * (quantity*-1)) AS total_closed FROM Trade NATURAL JOIN Turn WHERE close_ts IS NOT NULL;")[0]["total_closed"]
    @total_volume = Price.connection.select_all("select sum(abs(quantity)) from trade")[0]["sum"]
    @total_notional = Price.connection.select_all("select coalesce(sum(abs(quantity)*price)/2,0) from trade")[0]["coalesce"]
    
    @daily_stats = Price.connection.select_all("select * from daily_history order by day desc limit 5")
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
end
