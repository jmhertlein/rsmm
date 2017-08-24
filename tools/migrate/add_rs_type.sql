drop view daily_history;
alter table turn drop constraint turn_item_id_fkey;
alter table trade drop constraint trade_turn_id_fkey;
alter table quote drop constraint quote_item_id_fkey;
alter table price drop constraint price_item_id_fkey;

alter table item drop constraint item_pkey;
alter table turn drop constraint turn_pkey;
alter table trade drop constraint trade_pkey;
alter table quote drop constraint quote_pkey;
alter table price drop constraint price_pkey;

create type rs_type as enum ('rs3', 'osrs');

alter table item add column rs_type rs_type;
update item set rs_type='rs3';
alter table item add primary key (item_id, rs_type);

alter table turn add column rs_type rs_type;
update turn set rs_type='rs3';
alter table turn add primary key(turn_id, rs_type);
alter table turn add foreign key (item_id, rs_type) references Item(item_id, rs_type) on update cascade;

alter table trade add column rs_type rs_type;
update trade set rs_type='rs3';
alter table trade add primary key (turn_id, trade_ts, rs_type);
alter table trade add foreign key (turn_id, rs_type) references Turn(turn_id, rs_type);

alter table quote add column rs_type rs_type;
update quote set rs_type='rs3';
alter table quote add primary key (item_id, quote_ts, rs_type);
alter table quote add foreign key (item_id, rs_type) references item(item_id, rs_type) on update cascade;

alter table price add column rs_type rs_type;
update price set rs_type='rs3';
alter table price add primary key (item_id, day, rs_type);
alter table price add foreign key (item_id, rs_type) references item(item_id, rs_type) on update cascade;

create view daily_history as (select close_ts::date as day,
sum(price * quantity * -1) as closed_profit,
sum(abs(quantity))/2 as volume,
sum(price*abs(quantity))/2 as notional,
(sum(price*quantity*-1)/(sum(abs(quantity)/2))) as avg_profit_per_item
from trade natural join turn
group by day, rs_type);
