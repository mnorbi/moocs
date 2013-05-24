function G = StandardizeFactors(F)
	G = struct('var', [], 'card', [], 'val', []);
	for i = 1:length(F)
		G(i) = StandardizeFactor(F(i));
	end
end;
