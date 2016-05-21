class TradedDayTracker
  def initialize conn
    @conn = conn
  end

  def day_traded? date=Date.today
    @conn.exec_params('SELECT day FROM TradedDay WHERE day=$1', [date]) do |res|
      return res.ntuples != 0
    end
  end

  def record_traded_day date=Date.today
    @conn.exec_params('INSERT INTO TradedDay VALUES($1)', [date])
  end
end
