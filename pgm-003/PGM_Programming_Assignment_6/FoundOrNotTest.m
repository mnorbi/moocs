fMarket.var =  1;
fMarket.card =  3;
fMarket.val = [0.5 0.3 0.2];

fSurveyGivenMarket.var = [2 1];
fSurveyGivenMarket.card = [3 3];
fSurveyGivenMarket.val = [0.6 0.3 0.1 0.3 0.4 0.3 0.1 0.4 0.5];

fUtilityGivenMarket.var = [3 1];
fUtilityGivenMarket.card = [2 3];
fUtilityGivenMarket.val = [0 -7 0 5 0 20];

fDecisionFactor.var = [3 2];
fDecisionFactor.card = [2 3];
fDecisionFactor.val = ones(1, 6);

%PrintFactor(FactorMarginalization(FactorProduct(FactorProduct(fMarket, fSurveyGivenMarket), fUtilityGivenMarket), [1]));

I.RandomFactors = [fMarket fSurveyGivenMarket];
I.UtilityFactors = [fUtilityGivenMarket];
I.DecisionFactors = [fDecisionFactor];

[MEU OptimalDecisionRule] = OptimizeMEU(I)
PrintFactor(OptimalDecisionRule);