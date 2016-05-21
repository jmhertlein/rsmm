require_relative 'summary.rb'
def summarize prices, name, item_id
  history = prices.prices_for item_id

  last_px = history[-1]
  day = history.length - 2
  moves_down = 0
  while day >= 0 && history[day] > last_px
    moves_down += 1
    last_px = history[day]
    day -= 1
  end

  moves_up = 0
  while day >= 0 && history[day] < last_px
    moves_up += 1
    last_px = history[day]
    day -= 1
  end

  puts "#{moves_down} moves down"
  puts "#{moves_up} moves up"

  action = :nothing
  reason = nil
  if moves_down > 1
    reason = "Price is going down, don't buy."
  elsif moves_down == 1
    if moves_up > 1
      reason = "Turn is complete, sell."
      action = :sell
    else
      reason = "Price is waffling up and down w/ no ft"
    end
  elsif moves_down == 0
    if moves_up == 1
      reason = "Prepare to buy, price went up once"
    elsif moves_up == 2
      reason = "Initial buy-in!"
      action = :buy
    elsif moves_up > 2
      reason = "Keep buying."
      action = :buy
    end
  end

  summary = Summary.new name, item_id, action, reason, history[-1]
  return summary
end
