load TestI0;

[DefaultMEU DefaultOptimalDecisionRule] = OptimizeWithJointUtility(TestI0)
%DefaultMEU = -350.43
%DefaultOptimalDecisionRule =
%
%  scalar structure containing the fields:
%
%    var =  9
%    card =  2
%    val =
%
%       0   1

%PrintFactor(TestI0.RandomFactors(10));
%Default
%var 11      			var 1
%ARVD not indicated		not present       	0.750000
%ARVD indicated			not present			0.250000
%ARVD not indicated		present   			0.001000
%ARVD indicated     	present   			0.999000

%connect 11 test node with the decision node 9
TestI0.DecisionFactors(1) = struct('var', [9 11], 'card', [2 2], 'val', eye(2)(:)');

%T1
T1 = TestI0;%this is the default
[T1MEU T1OptimalDecisionRule] = OptimizeWithJointUtility(T1)
%T1MEU =  155.17
%T1OptimalDecisionRule =
%
%  scalar structure containing the fields:
%
%    var =
%
%        9   11
%
%    card =
%
%       2   2
%
%    val =
%
%       1   0   0   1


%T2
T2 = TestI0;
T2.RandomFactors(10).val = [0.999000 0.0010000 0.2500000 0.7500000];
[T2MEU T2OptimalDecisionRule] = OptimizeWithJointUtility(T2)

%T2MEU = -216.46
%T2OptimalDecisionRule =
%
%  scalar structure containing the fields:
%
%    var =
%
%        9   11
%
%    card =
%
%       2   2
%
%    val =
%
%       1   0   0   1



%T3
T3 = TestI0;
T3.RandomFactors(10).val = [0.999000 0.0010000 0.0010000 0.999000];
[T3MEU T3OptimalDecisionRule] = OptimizeWithJointUtility(T3)

%T3MEU =  323.75
%T3OptimalDecisionRule =
%
%  scalar structure containing the fields:
%
%    var =
%
%        9   11
%
%    card =
%
%       2   2
%
%    val =
%
%       1   0   0   1

VPIT1 = T1MEU-DefaultMEU;
T1Price = exp(VPIT1/100)-1
%T1Price =  155.97

VPIT2 = T2MEU-DefaultMEU;
T2Price = exp(VPIT2/100)-1
%T2Price =  2.8180

VPIT3 = T3MEU-DefaultMEU;
T3Price = exp(VPIT3/100)-1
%T3Price =  846.15

