CREATE TYPE rs_type AS ENUM ('rs3', 'osrs');
create type process as enum ('pxmon', 'itemdl', 'limitmon');

CREATE TABLE IF NOT EXISTS Item(
  item_name TEXT NOT NULL,
  ge_limit INTEGER,
  item_id INTEGER,
  rs_type rs_type,
  favorite BOOLEAN NOT NULL DEFAULT false,
  last_update_ts TIMESTAMP NOT NULL DEFAULT now(),
  PRIMARY KEY(item_id, rs_type)
);

CREATE TABLE IF NOT EXISTS Turn(
  turn_id SERIAL,
  rs_type rs_type,
  open_ts TIMESTAMP NOT NULL DEFAULT now(),
  close_ts TIMESTAMP,
  item_id INTEGER NOT NULL,
  PRIMARY KEY (turn_id, rs_type),
  FOREIGN KEY (item_id, rs_type) REFERENCES Item(item_id, rs_type) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Trade(
  turn_id INTEGER,
  trade_ts TIMESTAMP NOT NULL DEFAULT now(),
  rs_type rs_type,
  price INTEGER NOT NULL,
  quantity INTEGER NOT NULL,
  PRIMARY KEY(turn_id, trade_ts, rs_type),
  FOREIGN KEY (turn_id, rs_type) REFERENCES Turn(turn_id, rs_type)
);

CREATE TABLE IF NOT EXISTS Quote(
  quote_ts TIMESTAMP NOT NULL DEFAULT now(),
  rs_type rs_type,
  bid1 INTEGER NOT NULL,
  ask1 INTEGER NOT NULL,
  item_id INTEGER,
  synthetic BOOLEAN NOT NULL,
  PRIMARY KEY(item_id, quote_ts, rs_type),
  FOREIGN KEY (item_id, rs_type) references Item(item_id, rs_type) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Price(
  item_id INTEGER NOT NULL,
  day DATE NOT NULL,
  rs_type rs_type,
  price INTEGER NOT NULL,
  created_ts TIMESTAMP DEFAULT now(),
  PRIMARY KEY(item_id, day, rs_type),
  FOREIGN KEY (item_id, rs_type) REFERENCES Item(item_id, rs_type) ON UPDATE CASCADE
);

create view daily_history as (select close_ts::date as day, rs_type,
sum(price * quantity * -1) as closed_profit,
sum(abs(quantity))/2 as volume,
sum(price*abs(quantity))/2 as notional,
(sum(price*quantity*-1)/(sum(abs(quantity)/2))) as avg_profit_per_item

from trade natural join turn
group by day, rs_type);

create table process_result(
  run_id serial primary key,
  process process not null,
  rs_type rs_type not null,
  duration tsrange not null,
  success boolean not null,
  summary text not null
);

create table process_exception(
  run_id integer primary key references process_result(run_id),
  error_type text not null,
  message text not null,
  stack_trace text not null
);

create table pxmon_result(
  run_id integer primary key references process_result(run_id),
  prices_found integer not null
);

create table itemdl_result(
  run_id integer primary key references process_result(run_id),
  start_count integer not null,
  end_count integer not null,
  items_processed integer not null
);

create table limitmon_result(
  run_id integer primary key references process_result(run_id),
  items_scraped integer not null,
  updated_items integer not null,
  non_matching_items integer not null
);

create table limitmon_result_item(
  run_id integer references limitmon_result(run_id),
  item_id integer not null,
  old_limit integer,
  new_limit integer not null,
  primary key (run_id, item_id)
);
