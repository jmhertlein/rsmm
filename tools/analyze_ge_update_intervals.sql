select cur.day, age(min(cur.created_ts), min(prev.created_ts)) from price as cur, price as prev where prev.day = cur.day-1 group by cur.day order by cur.day desc;
