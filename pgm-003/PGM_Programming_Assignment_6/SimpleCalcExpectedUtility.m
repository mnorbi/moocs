% Copyright (C) Daphne Koller, Stanford University, 2012

function EU = SimpleCalcExpectedUtility(I)

  % Inputs: An influence diagram, I (as described in the writeup).
  %         I.RandomFactors = list of factors for each random variable.  These are CPDs, with
  %              the child variable = D.var(1)
  %         I.DecisionFactors = factor for the decision node.
  %         I.UtilityFactors = list of factors representing conditional utilities.
  % Return Value: the expected utility of I
  % Given a fully instantiated influence diagram with a single utility node and decision node,
  % calculate and return the expected utility.  Note - assumes that the decision rule for the 
  % decision node is fully assigned.

  % In this function, we assume there is only one utility node.
  F = [I.RandomFactors I.DecisionFactors];
  U = I.UtilityFactors(1);
  EU = [];
  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  %
  % YOUR CODE HERE
  %
  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  randomVars = unique([F.var]);%collect all variables from the random factors, these could be random variables and decision variables also
  decisionVars = unique([I.DecisionFactors.var]);%collect all variables involved in decision factors
  decisionVariableFactors = VariableElimination([F U], setdiff(randomVars, decisionVars));%eliminate all variables except variables involved in decision factors
  %compute the final factor, from the remaining factors (these involve only decision factors)
  prod = decisionVariableFactors(1);
  for i=2:length(decisionVariableFactors),
	prod = FactorProduct(prod,decisionVariableFactors(i));
  end;
  EU = sum(prod.val);
end
