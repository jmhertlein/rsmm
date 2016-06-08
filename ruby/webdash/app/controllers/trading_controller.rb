include ActionView::Helpers::NumberHelper

class TradingController < ApplicationController
  helper_method :commas
  def index
    @total_profit = Price.connection.select_all("SELECT SUM(price * (quantity*-1)) AS total_closed FROM Trade NATURAL JOIN Turn WHERE close_ts IS NOT NULL;")[0]["total_closed"]
    @total_volume = Price.connection.select_all("select sum(abs(quantity)) from trade")[0]["sum"]
    @total_notional = Price.connection.select_all("select coalesce(sum(abs(quantity)*price),0) from trade")[0]["coalesce"]

    @daily_closed = Price.connection.select_all("select coalesce(sum(quantity * price * -1),0) as closed_profit from turn natural join trade where close_ts > now()::date;").first["closed_profit"]
    @daily_volume = Price.connection.select_all("select coalesce(sum(abs(quantity)),0) from trade where trade_ts > now()::date;")[0]["coalesce"]
    @daily_notional = Price.connection.select_all("select coalesce(sum(abs(quantity)*price),0) from trade where trade_ts > now()::date;")[0]["coalesce"]
    @daily_closed_volume = Price.connection.select_all("select coalesce(sum(quantity*price*-1),0) from turn natural join trade where close_ts > now()::date;")[0]["coalesce"]
    if @daily_closed_volume.to_i != 0
      @daily_avg_profit_per_item = @daily_closed.to_i / (@daily_closed_volume.to_f/2)
    else
      @daily_avg_profit_per_item = "0"
    end
    @quotes = Price.connection.select_all("select count(*) from quote where quote_ts > now()::date")[0]["count"]
  end

  def commas s
    return number_with_delimiter(s, :delimiter => ',')
  end
end
