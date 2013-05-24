function G = FactorSums(F)
	if length(F) > 0,
		G = F(1);
		for i = 2:length(F)
			G = FactorSum(G, F(i));
		end;
	end;
end;