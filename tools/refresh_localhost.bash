#!/usr/bin/bash
psql -d rsmm -h localhost rsmm < ../java/src/main/resources/drop_all.sql
psql -d rsmm -h localhost rsmm < ../java/src/main/resources/setup.sql
psql -d rsmm -h claudius rsmm -c "copy (select item_name, ge_limit, item_id, rs_type, favorite, last_update_ts from item) to stdout" | psql -d rsmm -h localhost rsmm -c "copy item from stdin"
