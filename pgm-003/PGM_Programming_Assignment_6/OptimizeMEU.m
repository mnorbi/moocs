% Copyright (C) Daphne Koller, Stanford University, 2012

function [MEU OptimalDecisionRule] = OptimizeMEU( I )

  % Inputs: An influence diagram I with a single decision node and a single utility node.
  %         I.RandomFactors = list of factors for each random variable.  These are CPDs, with
  %              the child variable = D.var(1)
  %         I.DecisionFactors = factor for the decision node.
  %         I.UtilityFactors = list of factors representing conditional utilities.
  % Return value: the maximum expected utility of I and an optimal decision rule 
  % (represented again as a factor) that yields that expected utility.
  
  % We assume I has a single decision node.
  % You may assume that there is a unique optimal decision.
  D = I.DecisionFactors(1);

  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  %
  % YOUR CODE HERE...
  % 
  % Some other information that might be useful for some implementations
  % (note that there are multiple ways to implement this):
  % 1.  It is probably easiest to think of two cases - D has parents and D 
  %     has no parents.
  % 2.  You may find the Matlab/Octave function setdiff useful.
  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    
  OptimalDecisionRule.var = D.var;
  OptimalDecisionRule.card = D.card;
  
  EUF = CalculateExpectedUtilityFactor(I);
  
  % from EUF factor table, for each assignment find for each row the maximum value and create a max indicator matrix
  %
  %				decision1	decision2
  % assignment1		1		2
  % assignment2		3		0
  %
  %				decision1	decision2
  % assignment1		0		1
  % assignment2		1		0

  EUF = ReorderFactor(EUF, D.var(1));% put the decision variable at the first place
  reshaped = reshape(EUF.val, EUF.card(1), prod(EUF.card(2:end)))';%assignments x decision shaped matrix
  [maxVals idxs] = max(reshaped, [], 2);%find for each row the maximum value and it's respective idx
  OptimalDecisionRule.val = eye(EUF.card(1))(idxs, :)'(:)';%convert indexes into a matrix of indicator vectors and convert the matrix into a vector
  MEU = sum(maxVals);
  OptimalDecisionRule = StandardizeFactors(OptimalDecisionRule);
  
end;
