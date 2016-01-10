CREATE TABLE IF NOT EXISTS Item(
  item_name TEXT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS Turn(
  turn_id SERIAL PRIMARY KEY,
  item_name TEXT REFERENCES Item(item_name),
  open_ts TIMESTAMP NOT NULL DEFAULT now(),
  close_ts TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Trade(
  turn_id INTEGER REFERENCES Turn(turn_id),
  trade_ts TIMESTAMP NOT NULL DEFAULT now(),
  price INTEGER NOT NULL,
  quantity INTEGER NOT NULL,
  PRIMARY KEY(turn_id, trade_ts)
);

CREATE TABLE IF NOT EXISTS Quote(
  item_name TEXT REFERENCES Item(item_name),
  quote_ts TIMESTAMP NOT NULL DEFAULT now(),
  bid1 INTEGER NOT NULL,
  ask1 INTEGER NOT NULL,
  PRIMARY KEY(item_name, quote_ts)
);