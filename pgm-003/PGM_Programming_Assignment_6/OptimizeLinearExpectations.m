% Copyright (C) Daphne Koller, Stanford University, 2012

function [MEU OptimalDecisionRule] = OptimizeLinearExpectations( I )
  % Inputs: An influence diagram I with a single decision node and one or more utility nodes.
  %         I.RandomFactors = list of factors for each random variable.  These are CPDs, with
  %              the child variable = D.var(1)
  %         I.DecisionFactors = factor for the decision node.
  %         I.UtilityFactors = list of factors representing conditional utilities.
  % Return value: the maximum expected utility of I and an optimal decision rule 
  % (represented again as a factor) that yields that expected utility.
  % You may assume that there is a unique optimal decision.
  %
  % This is similar to OptimizeMEU except that we will have to account for
  % multiple utility factors.  We will do this by calculating the expected
  % utility factors and combining them, then optimizing with respect to that
  % combined expected utility factor.  
  MEU = [];
  OptimalDecisionRule = [];
  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  %
  % YOUR CODE HERE
  %
  % A decision rule for D assigns, for each joint assignment to D's parents, 
  % probability 1 to the best option from the EUF for that joint assignment 
  % to D's parents, and 0 otherwise.  Note that when D has no parents, it is
  % a degenerate case we can handle separately for convenience.
  %
  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  D = I.DecisionFactors(1);
  OptimalDecisionRule.var = D.var;
  OptimalDecisionRule.card = D.card;
  
  EUF = struct('var', [], 'card', [], 'val', []);
  for i=1:length(I.UtilityFactors),
	I2 = I;
	I2.UtilityFactors = I.UtilityFactors(i);
	EUF = FactorSum(EUF, CalculateExpectedUtilityFactor(I2));
  end;
  
  %Taken from OptimizeMEU
  EUF = ReorderFactor(EUF, D.var(1));% put the decision variable at the first place
  reshaped = reshape(EUF.val, EUF.card(1), prod(EUF.card(2:end)))';%assignments x decision shaped matrix
  [maxVals idxs] = max(reshaped, [], 2);%find for each row the maximum value and it's respective idx
  OptimalDecisionRule.val = eye(EUF.card(1))(idxs, :)'(:)';%convert indexes into a matrix of indicator vectors and convert the matrix into a vector
  MEU = sum(maxVals);
  OptimalDecisionRule = StandardizeFactors(OptimalDecisionRule);

end
