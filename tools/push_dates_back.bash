#!/usr/bin/bash

if [ $1 == "" ] ; then
  echo "Please provide number of days to push back."
  exit
fi

function push_ts {
  psql -d rsmm rsmm -c "update ""$1"" set ""$2""=(""$2""-interval '""$3"" days') where ""$2""::date = now()::date;"
}

push_ts quote quote_ts "$1"
push_ts turn open_ts "$1"
push_ts turn close_ts "$1"
push_ts trade trade_ts "$1"
