select sum(A.value*B.value) from A,B where A.col_num = B.row_num and A.row_num = 2 and B.col_num = 3;-- group by A.row_num, B.col_num;
