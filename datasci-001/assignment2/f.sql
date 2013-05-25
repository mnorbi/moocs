select count(*) from (select distinct f.docid from frequency f where f.term = 'transactions' intersect select distinct f2.docid from frequency f2 where f2.term = 'world');
