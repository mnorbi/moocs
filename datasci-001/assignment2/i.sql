create temp view if not exists freq_w_query as SELECT * FROM frequency
UNION
SELECT 'q' as docid, 'washington' as term, 1 as count 
UNION
SELECT 'q' as docid, 'taxes' as term, 1 as count
UNION 
SELECT 'q' as docid, 'treasury' as term, 1 as count;

select max(s) from (
	select sum(f.count) s from freq_w_query f, freq_w_query fq where fq.term = f.term and fq.docid = 'q' and f.docid != 'q' group by f.docid);
--select f.docid, sum(f.count) from freq_w_query fq, freq_w_query f where fq.docid = 'q' and fq.term = f.term;
