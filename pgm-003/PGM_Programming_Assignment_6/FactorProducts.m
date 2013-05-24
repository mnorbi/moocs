function Fprod = FactorProducts(F)
	if length(F) > 0,
		Fprod = F(1);
		for i= 2:length(F),
			Fprod = FactorProduct(Fprod, F(i));
		end;
	end;
end;
