select count(*) from (select count(distinct docid) from frequency f group by docid having sum(f.count) > 300);
