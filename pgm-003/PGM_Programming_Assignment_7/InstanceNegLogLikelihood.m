% function [nll, grad] = InstanceNegLogLikelihood(X, y, theta, modelParams)
% returns the negative log-likelihood and its gradient, given a CRF with parameters theta,
% on data (X, y). 
%
% Inputs:
% X            Data.                           (numCharacters x numImageFeatures matrix)
%              X(:,1) is all ones, i.e., it encodes the intercept/bias term.
% y            Data labels.                    (numCharacters x 1 vector)
% theta        CRF weights/parameters.         (numParams x 1 vector)
%              These are shared among the various singleton / pairwise features.
% modelParams  Struct with three fields:
%   .numHiddenStates     in our case, set to 26 (26 possible characters)
%   .numObservedStates   in our case, set to 2  (each pixel is either on or off)
%   .lambda              the regularization parameter lambda
%
% Outputs:
% nll          Negative log-likelihood of the data.    (scalar)
% grad         Gradient of nll with respect to theta   (numParams x 1 vector)
%
% Copyright (C) Daphne Koller, Stanford Univerity, 2012

function [nll, grad] = InstanceNegLogLikelihood(X, y, theta, modelParams)

    % featureSet is a struct with two fields:
    %    .numParams - the number of parameters in the CRF (this is not numImageFeatures
    %                 nor numFeatures, because of parameter sharing)
    %    .features  - an array comprising the features in the CRF.
    %
    % Each feature is a binary indicator variable, represented by a struct 
    % with three fields:
    %    .var          - a vector containing the variables in the scope of this feature
    %    .assignment   - the assignment that this indicator variable corresponds to
    %    .paramIdx     - the index in theta that this feature corresponds to
    %
    % For example, if we have:
    %   
    %   feature = struct('var', [2 3], 'assignment', [5 6], 'paramIdx', 8);
    %
    % then feature is an indicator function over X_2 and X_3, which takes on a value of 1
    % if X_2 = 5 and X_3 = 6 (which would be 'e' and 'f'), and 0 otherwise. 
    % Its contribution to the log-likelihood would be theta(8) if it's 1, and 0 otherwise.
    %
    % If you're interested in the implementation details of CRFs, 
    % feel free to read through GenerateAllFeatures.m and the functions it calls!
    % For the purposes of this assignment, though, you don't
    % have to understand how this code works. (It's complicated.)
    
    featureSet = GenerateAllFeatures(X, modelParams);

    % Use the featureSet to calculate nll and grad.
    % This is the main part of the assignment, and it is very tricky - be careful!
    % You might want to code up your own numerical gradient checker to make sure
    % your answers are correct.
    %
    % Hint: you can use CliqueTreeCalibrate to calculate logZ effectively. 
    %       We have halfway-modified CliqueTreeCalibrate; complete our implementation 
    %       if you want to use it to compute logZ.
    
    nll = 0;
    grad = zeros(size(theta));
    %%%
    % Your code here:
    
	%1. create and calibrate clique tree
	[CliqueTree logZ] = CreateConsolidatedCalibratedCliqueTree(X, y, theta, modelParams, featureSet);
	
	%2. weightedFeatureCounts, dataFeatureCounts, modelExpectedFeatureCounts
	dataFeatureCounts = DataFeatureCounts(y, featureSet, theta);
	weightedFeatureCounts = WeightedFeatureCounts(dataFeatureCounts, theta);
	modelExpectedFeatureCounts = ModelExpectedFeatureCounts(CliqueTree, featureSet);
	
	%3. compute nll and grad from *Counts computed in step 2.
	nll = logZ - weightedFeatureCounts + RegularizationCost(theta, modelParams.lambda);
	
	grad = modelExpectedFeatureCounts - dataFeatureCounts + modelParams.lambda .* theta;
	
end

%This is the naive aproach for creating the clique tree.
%We create from each feature a factor
%and we let the CreateCliqueTree function
%to create the clique tree
function [CT logZ] = CreateSimpleCalibratedCliqueTree(X, y, theta, modelParams, featureSet)
	n = length(featureSet.features);
	factors = repmat(struct('var', [], 'card', [], 'val', []), n, 1);
	for i = 1:n
		feature = featureSet.features(i);
		
		factors(i).var = feature.var;
		factors(i).card = repmat([modelParams.numHiddenStates], 1, length(feature.var));
		factors(i).val = zeros(1, prod(factors(i).card));
		factors(i).val(AssignmentToIndex(feature.assignment, factors(i).card)) = theta(feature.paramIdx);
	end
	
	for i = 1:length(factors)
		%convert back from log space each potential
		factors(i).val = exp(factors(i).val);
	end
	
	[CT logZ] = CliqueTreeCalibrate(CreateCliqueTree(factors), false);

end

% This is an optimized version of the clique tree creation.
% Y is a word like Y1Y2Y3 = "cat", for each adjacent character pair
% we will have a clique, like clique1(Y1Y2) "ca" and clique2(Y2Y3) "at"
% so for a word with length 3 we will have a clique with length 2
% the factors (created from features) will be assigned to one of the cliques
% this will be taken care of by the CreateCliqueTree.
% We could create the clique tree from the small feature factors, giving them
% as input to CreateCliqueTree but that will be slow, so we first consolidate
% the small featurefactors into factorY1, factorY2, ..., factorY1Y2, factorY2Y3, ...
% bigger factors.
function [CT logZ] = CreateConsolidatedCalibratedCliqueTree(X, y, theta, modelParams, featureSet)
	n = length(y);
	%consolidated factors
	factors = repmat(struct('var', [], 'card', [], 'val', []), 2*length(y)-1, 1);
	%factorY1, factorY2, ...
	for i = 1:n
		factors(i).var = i;
		factors(i).card = [modelParams.numHiddenStates];
		factors(i).val = zeros(1, prod(factors(i).card));
	end
	%factorY1Y2, factorY2Y3, ...
	for i = 1:n-1
		idx = n+i;
		factors(idx).var = [i, i+1];
		factors(idx).card = [modelParams.numHiddenStates, modelParams.numHiddenStates];
		factors(idx).val = zeros(1, prod(factors(idx).card));
	end
	
	for i = 1:length(featureSet.features)
		%each feature has to be converted into a factor
		%and has to be merged into a consolidated factor
		%each feature says something about the variables 
		%inside the feature.var. feature.var has
		%the form of [Yi] or %[Yi,Yi+1]
		feature = featureSet.features(i);
		%find the consolidated factor
		%example: feature.var = [1] --> factors(1), feature.var = [2 3] --> factors(n+2) where n is length(y)
		idx = [feature.var(1), n+feature.var(1)](length(feature.var));
		factors(idx).val(AssignmentToIndex(feature.assignment, factors(idx).card)) += theta(feature.paramIdx);
	end
	
	for i = 1:length(factors)
		%convert back from log space each potential
		factors(i).val = exp(factors(i).val);
	end
	
	[CT logZ] = CliqueTreeCalibrate(CreateCliqueTree(factors), false);
	
end

function WFC = WeightedFeatureCounts(dataFeatureCounts, theta)
	WFC = sum(dataFeatureCounts .* theta);
end

function RC = RegularizationCost(theta, lambda)
	RC = lambda/2*sum(theta.^2);
end

function MEFC = ModelExpectedFeatureCounts(CliqueTree, featureSet)
	MEFC = zeros(1,featureSet.numParams);
	factors = struct();
	for i = 1:length(featureSet.features)
		feature = featureSet.features(i);
		factorId = int2str(feature.var);
		aFactor = [];
		if isfield(factors, factorId)
			aFactor = getfield(factors, factorId);
		end
		if isempty(aFactor)
			%compute factor
			for i = 1:length(CliqueTree.cliqueList)
				clique = CliqueTree.cliqueList(i);
				if ~length(setdiff(feature.var, clique.var))
					%compute and store factor for later use
					aFactor = FactorMarginalization(clique, setdiff(clique.var, feature.var));
					%we have to normalize the vector
					aFactor.val = aFactor.val ./ sum(aFactor.val);
					factors = setfield(factors, factorId, aFactor);
					break;
				end
			end
		end
		MEFC(feature.paramIdx) += aFactor.val(AssignmentToIndex(feature.assignment, aFactor.card));
	end
end

%feature indicator vector
function DFC = DataFeatureCounts(y, featureSet, theta)
	%we take only those features where feature characters exactly match input word characters
	activeFeatures = arrayfun(@(feature) ~any(y(feature.var) - feature.assignment), featureSet.features);
	%for each feature active feature, we take the paramIdx value that indexes the theta vector, we take each value only once
	idxs = unique(horzcat(featureSet.features.paramIdx) .* activeFeatures);
	%remove 0, this came in because of the omitted features
	idxs = idxs(find(idxs~=0, 1):end);
	DFC = zeros(1,length(theta));
	DFC(idxs) = 1;
end
