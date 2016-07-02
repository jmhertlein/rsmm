class ProcessController < ApplicationController
  def index
    @items_tracked = 0
    @last_ge_update_ts = "00:00:00"
    @current_ge_date = "2016-07-1"
    @limits_tracked = 0
  end
end
