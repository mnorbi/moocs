function [P loglikelihood] = LearnCPDsGivenGraph(dataset, G, labels)
%
% Inputs:
% dataset: N x 10 x 3, N poses represented by 10 parts in (y, x, alpha)
% G: graph parameterization as explained in PA description
% labels: N x 2 true class labels for the examples. labels(i,j)=1 if the 
%         the ith example belongs to class j and 0 elsewhere        
%
% Outputs:
% P: struct array parameters (explained in PA description)
% loglikelihood: log-likelihood of the data (scalar)
%
% Copyright (C) Daphne Koller, Stanford Univerity, 2012

N = size(dataset, 1);
K = size(labels,2);

loglikelihood = 0;
P.c = zeros(1,K);

% estimate parameters
% fill in P.c, MLE for class probabilities
% fill in P.clg for each body part and each class
% choose the right parameterization based on G(i,1)
% compute the likelihood - you may want to use ComputeLogLikelihood.m
% you just implemented.
%%%%%%%%%%%%%%%%%%%%%%%%%
% YOUR CODE HERE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
P.clg = repmat(struct('mu_y',[],'mu_x',[],'mu_angle',[],'sigma_y',[],'sigma_x',[],'sigma_angle',[],'theta',[]), 1, size(G,1));
for k=1:K
	if ndims(G) == 2
		Gk = G;
	else
		Gk = G(:,:,k);
	end
	filter = labels(:,k) == 1;
	P.c(k) = sum(filter);
	fdataset = dataset(find(filter),:,:);
	for i=1:size(Gk,1)
		if Gk(i,1)%class and another body part is parent, equations 5,6,7 hold
			U = squeeze(fdataset(:,Gk(i,2),:));
			
			[theta P.clg(i).sigma_y(k)] = FitLinearGaussianParameters(fdataset(:,i,1), U);
			P.clg(i).theta(k, [1:4]) = theta'([4 1:3]);
			
			[theta P.clg(i).sigma_x(k)] = FitLinearGaussianParameters(fdataset(:,i,2), U);
			P.clg(i).theta(k, [5:8]) = theta'([4 1:3]);
			
			[theta P.clg(i).sigma_angle(k)] = FitLinearGaussianParameters(fdataset(:,i,3), U);
			P.clg(i).theta(k, [9:12]) = theta'([4 1:3]);
		else%only class is the parent, equations 2,3,4 hold
			[P.clg(i).mu_y(k), P.clg(i).sigma_y(k)] = FitGaussianParameters(fdataset(:,i,1));
			[P.clg(i).mu_x(k), P.clg(i).sigma_x(k)] = FitGaussianParameters(fdataset(:,i,2));
			[P.clg(i).mu_angle(k), P.clg(i).sigma_angle(k)] = FitGaussianParameters(fdataset(:,i,3));
		end
	end
end
P.c = P.c ./ sum(P.c);
loglikelihood = ComputeLogLikelihood(P, G, dataset);
%fprintf('log likelihood: %f\n', loglikelihood);
end