function accuracy = ClassifyDataset(dataset, labels, P, G)
% returns the accuracy of the model P and graph G on the dataset 
%
% Inputs:
% dataset: N x 10 x 3, N test instances represented by 10 parts
% labels:  N x 2 true class labels for the instances.
%          labels(i,j)=1 if the ith instance belongs to class j 
% P: struct array model parameters (explained in PA description)
% G: graph structure and parameterization (explained in PA description) 
%
% Outputs:
% accuracy: fraction of correctly classified instances (scalar)
%
% Copyright (C) Daphne Koller, Stanford Univerity, 2012

N = size(dataset, 1);
accuracy = 0.0;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% YOUR CODE HERE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	K = length(P.c);
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
	[_ IDX] = max(PJointLog, [], 2);
	ModelClassification = [1:K] == IDX;
	accuracy = sum(all(ModelClassification == labels,2))/N;
	%fprintf('Accuracy: %.2f\n', accuracy);
end
function VAL = lognormpdfs(X, MU, SIGMA)
	VAL = - (X - MU).^2 ./ (2*SIGMA^2) - log (sqrt(2*pi) * SIGMA);
end