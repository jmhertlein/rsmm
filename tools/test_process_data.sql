insert into process_result values(1, 'pxmon', 'osrs', tsrange('2017-09-10 00:01:00', '2017-09-10 00:02:00'), true, '10 prices found.');
insert into process_result values(2, 'pxmon', 'osrs', tsrange('2017-09-10 01:01:00', '2017-09-10 01:02:00'), false, '11 prices found.');
insert into process_result values(3, 'pxmon', 'osrs', tsrange('2017-09-10 02:01:00', '2017-09-10 04:02:00'), true, '12 prices found.');
insert into process_result values(4, 'pxmon', 'osrs', tsrange('2017-09-10 03:01:00', '2017-09-10 04:02:00'), true, '13 prices found.');

insert into pxmon_result values(1, 10);
insert into pxmon_result values(3, 12);
insert into pxmon_result values(4, 13);

insert into process_exception values(2, 'DangError', 'Super broken!!', 'something.rb: line 21\nsomethingelse.rb: line 22\nathirdthing.rb: line 12');
