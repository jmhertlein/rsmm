#!/usr/bin/bash
psql -d rsmm -h localhost rsmm < ./src/main/resources/drop_all.sql
psql -d rsmm -h localhost rsmm < ./src/main/resources/setup.sql
psql -d rsmm -h claudius rsmm -c "copy (select item_id, item_name, ge_limit, favorite from item) to stdout" | psql -d rsmm -h localhost rsmm -c "copy item from stdin"
