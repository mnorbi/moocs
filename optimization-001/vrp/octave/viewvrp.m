function route=viewvrp(n)

vrpname=sprintf('%d.vrp',n);
solname=sprintf('%d.sol',n);
t=dlmread(vrpname);
vcap=t(1,3)
cust.x=t(2:end,2);
cust.y=t(2:end,3);
cust.d=t(2:end,1);
fid=fopen(solname,'r');
tmp=fgets(fid);
clear route;
routeidx=1;
while ischar(tmp)
    tmp=fgets(fid);
    if (tmp~=-1)
        route(routeidx).v=sscanf(tmp,'%d')+1;    
        routeidx=routeidx+1;
    end
end
fclose(fid);

fig1=figure(1);
scatter(cust.x,cust.y);
hold on;
coloridx=(1:routeidx)/routeidx;
totaldist=0;
for ii=1:length(route)
	xx=cust.x(route(ii).v);
	yy=cust.y(route(ii).v);
	rd=routedist(xx,yy);
	totaldist=totaldist+rd;
	td=sum(cust.d(route(ii).v));
	plot(xx,yy,'Color',[vcap<td,vcap>=td,0]);
end
totaldist
fflush(stdout);
hold off;

for kk=1:100
	[x,y,k]=ginput(2);
	if k(2)==toascii('e')
		break;
	end
	dx=cust.x-x(1);
	dy=cust.y-y(1);
	r2=dx.*dx+dy.*dy;
	[mv1 mi1]=min(r2);
	for ii=1:length(route)
		f1=find(route(ii).v==mi1);
		if ~isempty(f1)
			swr1=ii;
			swi1=f1;
			break;
		end
	end
	dx=cust.x-x(2);
	dy=cust.y-y(2);
	r2=dx.*dx+dy.*dy;
	[mv2 mi2]=min(r2);
	for ii=1:length(route)
		f2=find(route(ii).v==mi2);
		if ~isempty(f2)
			swr2=ii;
			swi2=f2;
			break;
		end
	end
	if (k(2)==toascii('i'))
		1
		route(swr1).v=[route(swr1).v(1:swi1-1);route(swr1).v(swi1+1:end)];
		route(swr2).v=[route(swr2).v(1:swi2-1);mi1;route(swr2).v(swi2:end)];
	else
		2
		route(swr1).v(swi1)=mi2;
		route(swr2).v(swi2)=mi1;
	end
	clf(fig1);
	scatter(cust.x,cust.y);
	hold on;
	coloridx=(1:routeidx)/routeidx;
	totaldist=0;
	for ii=1:length(route)
		xx=cust.x(route(ii).v);
		yy=cust.y(route(ii).v);
		rd=routedist(xx,yy);
		totaldist=totaldist+rd;
		td=sum(cust.d(route(ii).v));
		plot(xx,yy,'Color',[vcap<td,vcap>=td,0]);
	end
	totaldist
	fflush(stdout);
	hold off;
end