function r=routedist(x,y)
	dfx=diff(x);
	dfy=diff(y);
	r=sum(sqrt(dfx.*dfx+dfy.*dfy));