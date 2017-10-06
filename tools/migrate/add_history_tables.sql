begin;
create type process as enum ('pxmon', 'itemdl', 'limitmon');

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
commit;
