function loglikelihood = ComputeLogLikelihood(P, G, dataset)
	% returns the (natural) log-likelihood of data given the model and graph structure
	%
	% Inputs:
	% P: struct array parameters (explained in PA description)
	% G: graph structure and parameterization (explained in PA description)
	%
	%    NOTICE that G could be either 10x2 (same graph shared by all classes)
	%    or 10x2x2 (each class has its own graph). your code should compute
	%    the log-likelihood using the right graph.
	%
	% dataset: N x 10 x 3, N poses represented by 10 parts in (y, x, alpha)
	% 
	% Output:
	% loglikelihood: log-likelihood of the data (scalar)
	%
	% Copyright (C) Daphne Koller, Stanford Univerity, 2012

	N = size(dataset,1); % number of examples
	K = length(P.c); % number of classes

	loglikelihood = 0;
	% You should compute the log likelihood of data as in eq. (12) and (13)
	% in the PA description
	% Hint: Use lognormpdf instead of log(normpdf) to prevent underflow.
	%       You may use log(sum(exp(logProb))) to do addition in the original
	%       space, sum(Prob).
	%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	% YOUR CODE HERE
	%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	PJointLog = repmat(log(P.c),N,1);
	for k=1:K
		if ndims(G) == 2
			Gk = G;
		else
			Gk = G(:,:,k);
		end
		for i=1:size(Gk,1)
			if Gk(i,1)%class and another body part is parent, equations 5,6,7 hold
				dp = [ones(N,1) squeeze(dataset(:,Gk(i,2),:))];
				mu_y = dp*P.clg(i).theta(k,1:4)';
				mu_x = dp*P.clg(i).theta(k,5:8)';
				mu_angle = dp*P.clg(i).theta(k,9:12)';
			else%only class is the parent, equations 2,3,4 hold
				mu_y = P.clg(i).mu_y(k);
				mu_x = P.clg(i).mu_x(k);
				mu_angle = P.clg(i).mu_angle(k);
			end
			p_y = lognormpdfs(dataset(:,i,1),	 mu_y,		P.clg(i).sigma_y(k));
			p_x = lognormpdfs(dataset(:,i,2), 	 mu_x,		P.clg(i).sigma_x(k));
			p_angle = lognormpdfs(dataset(:,i,3),mu_angle, 	P.clg(i).sigma_angle(k));
			PJointLog(:,k) += p_y + p_x + p_angle;
		end
	end
	loglikelihood += sum(log(sum(exp(PJointLog),2)));
end

function VAL = lognormpdfs(X, MU, SIGMA)
	VAL = - (X - MU).^2 ./ (2*SIGMA^2) - log (sqrt(2*pi) * SIGMA);
end