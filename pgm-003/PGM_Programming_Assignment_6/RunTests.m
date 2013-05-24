function RuntTests
if (~exist('TS','var') || true)
  function v = normval(v)
    v /= sum(v);
  endfunction

  %% This is based on TestCases to make it all more testable.

  TS = repmat(struct('I', [], 'allDs', [], 'allEU', [], 'EUF', []), 1, 3);

  %% Test case 1.
  I.RandomFactors = struct('var', [1], 'card', [2], 'val', normval([7, 3]));
  I.DecisionFactors = struct('var', [2], 'card', [2], 'val', [1 0]);
  I.UtilityFactors = struct('var', [1, 2], 'card', [2, 2], 'val', [10, 1, 5, 1]);

  TS(1).I = I;
  TS(1).allDs = [1 0; 0 1];
  TS(1).EUF = struct('var', [2], 'card', [2], 'val', [7.3 3.8]);
  TS(1).allEU = [7.3 3.8];
  TS(1).MEU = 7.3;
  TS(1).OptDR = struct('var', [2], 'card', [2], 'val', [1 0]);

  %% Test case 2.
  I.RandomFactors = ...
      [struct('var', [1], 'card', [2], 'val', normval([7, 3])), ...
       CPDFromFactor(struct('var', [3,1,2], 'card', [2,2,2], 'val', [4 4 1 1 1 1 4 4]), 3)];
  I.DecisionFactors = struct('var', [2], 'card', [2], 'val', [1 0]);
  I.UtilityFactors = struct('var', [2,3], 'card', [2, 2], 'val', [10, 1, 5, 1]);

  TS(2).I = I;
  TS(2).allDs = [1 0; 0 1];
  TS(2).EUF = struct('var', [2], 'card', [2], 'val', [7.5 1.0]);
  TS(2).allEU = [7.5 1.0];
  TS(2).MEU = 7.5;
  TS(2).OptDR = struct('var', [2], 'card', [2], 'val', [1 0]);

  %% Test case 3.
  I.RandomFactors = ...
      [struct('var', [1], 'card', [2], 'val', normval([7, 3])), ...
       CPDFromFactor(struct('var', [3,1,2], 'card', [2,2,2], 'val', [4 4 1 1 1 1 4 4]), 3)];
  I.DecisionFactors = struct('var', [2,1], 'card', [2,2], 'val', [1,0,0,1]);
  I.UtilityFactors = struct('var', [2,3], 'card', [2, 2], 'val', [10, 1, 5, 1]);

  TS(3).I = I;
  TS(3).allDs = [1 0 1 0; 1 0 0 1; 0 1 1 0; 0 1 0 1];
  TS(3).EUF = struct('var', [1,2], 'card', [2 2], 'val', [5.25 2.25 0.7 0.3]);
  TS(3).allEU = [7.5 5.55 2.95 1.0];
  TS(3).MEU = 7.5;
  TS(3).OptDR = struct('var', [1,2], 'card', [2,2], 'val', [1,1,0,0]);
  
  %% Test case 4.
  I.RandomFactors = ...
      [struct('var', [1], 'card', [2], 'val', normval([7, 3])), ...
       CPDFromFactor(struct('var', [3,1,2], 'card', [2,2,2], 'val', [4 4 1 1 1 1 4 4]), 3)];
  I.DecisionFactors = struct('var', [2,1], 'card', [2,2], 'val', [1,0,0,1]);
  I.UtilityFactors = ...
      [struct('var', [2,3], 'card', [2, 2], 'val', [10, 1, 5, 1]), ...
       struct('var', [2], 'card', [2], 'val', [1, 10])];

  T4.I = I;
  T4.MEU = 11;
  T4.OptDR = struct('var', [1,2], 'card', [2,2], 'val', [0,0,1,1]);
  
endif

%% Change this to test different functions!
testNum = 5

switch testNum
  case 1
    for t = 1:length(TS)
      T = TS(t);
      for d = 1:size(T.allDs, 1)
	T.I.DecisionFactors.val = T.allDs(d, :);
	EU = SimpleCalcExpectedUtility(T.I);
	assert(EU, T.allEU(d), 1e-6);
      endfor
    endfor

  case 2
    for t = 1:length(TS)
      T = TS(t);
      EUF = CalculateExpectedUtilityFactor(T.I);
      assert(EUF, T.EUF, 1e-6);
    endfor

  case 3
    for t = 1:length(TS)
      T = TS(t);
      [meu optdr] = OptimizeMEU(T.I);
      assert(meu, T.MEU, 1e-6);
      assert(optdr, T.OptDR, 1e-6);
    endfor

  case 4
    [meu optdr] = OptimizeWithJointUtility(T4.I);
    assert(meu, T4.MEU, 1e-6);
    assert(optdr, T4.OptDR, 1e-6);
    %% Also, see if it works with single utility:
    for t = 1:length(TS)
      T = TS(t);
      [meu optdr] = OptimizeWithJointUtility(T.I);
      assert(meu, T.MEU, 1e-6);
      assert(optdr, T.OptDR, 1e-6);
    endfor

  case 5
    [meu optdr] = OptimizeLinearExpectations(T4.I);
    assert(meu, T4.MEU, 1e-6);
    assert(optdr, T4.OptDR, 1e-6);
    %% Also, see if it works with single utility:
    for t = 1:length(TS)
      T = TS(t);
      [meu optdr] = OptimizeLinearExpectations(T.I);
      assert(meu, T.MEU, 1e-6);
      assert(optdr, T.OptDR, 1e-6);
    endfor

endswitch

disp('Test finished successfully!');
end