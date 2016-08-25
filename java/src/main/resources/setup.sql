CREATE TABLE IF NOT EXISTS Item(
  item_name TEXT NOT NULL,
  ge_limit INTEGER,
  item_id INTEGER PRIMARY KEY,
  favorite BOOLEAN NOT NULL DEFAULT false,
  last_update_ts TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS Turn(
  turn_id SERIAL PRIMARY KEY,
  open_ts TIMESTAMP NOT NULL DEFAULT now(),
  close_ts TIMESTAMP,
  item_id INTEGER NOT NULL REFERENCES Item(item_id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Trade(
  turn_id INTEGER REFERENCES Turn(turn_id),
  trade_ts TIMESTAMP NOT NULL DEFAULT now(),
  price INTEGER NOT NULL,
  quantity INTEGER NOT NULL,
  PRIMARY KEY(turn_id, trade_ts)
);

CREATE TABLE IF NOT EXISTS Quote(
  quote_ts TIMESTAMP NOT NULL DEFAULT now(),
  bid1 INTEGER NOT NULL,
  ask1 INTEGER NOT NULL,
  item_id INTEGER REFERENCES Item(item_id) ON UPDATE CASCADE,
  synthetic BOOLEAN NOT NULL,
  PRIMARY KEY(item_id, quote_ts)
);

CREATE TABLE IF NOT EXISTS Price(
  item_id INTEGER NOT NULL REFERENCES Item(item_id) ON UPDATE CASCADE,
  day DATE NOT NULL,
  price INTEGER NOT NULL,
  created_ts TIMESTAMP DEFAULT now(),
  PRIMARY KEY(item_id, day)
);

create view daily_history as (select close_ts::date as day,
sum(price * quantity * -1) as closed_profit,
sum(abs(quantity))/2 as volume,
sum(price*abs(quantity))/2 as notional,
(sum(price*quantity*-1)/(sum(abs(quantity)/2))) as avg_profit_per_item

from trade natural join turn
group by day);
