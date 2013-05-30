function [Beta sigma] = FitLinearGaussianParameters(X, U)

% Estimate parameters of the linear Gaussian model:
% X|U ~ N(Beta(1)*U(1) + ... + Beta(n)*U(n) + Beta(n+1), sigma^2);

% Note that Matlab/Octave index from 1, we can't write Beta(0).
% So Beta(n+1) is essentially Beta(0) in the text book.

% X: (M x 1), the child variable, M examples
% U: (M x N), N parent variables, M examples
%
% Copyright (C) Daphne Koller, Stanford Univerity, 2012

M = size(U,1);
N = size(U,2);

Beta = zeros(N+1,1);
sigma = 1;

% collect expectations and solve the linear system
% A = [ E[U(1)],      E[U(2)],      ... , E[U(n)],      1     ; 
%       E[U(1)*U(1)], E[U(2)*U(1)], ... , E[U(n)*U(1)], E[U(1)];
%       ...         , ...         , ... , ...         , ...   ;
%       E[U(1)*U(n)], E[U(2)*U(n)], ... , E[U(n)*U(n)], E[U(n)] ]

% construct A
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% YOUR CODE HERE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
warning ("off", "Octave:broadcast");

%1 	1   1     1
%U1  U1' U1''  U1'''
%U2  U2' U2''  U2'''
%
%	U1   U2	    1	
%	U1'  U2'    1
%	U1'' U2''   1
%	U1'''U2'''  1
theOnes = ones(M, 1);
A = [theOnes U]'*[U theOnes]./M;

% B = [ E[X]; E[X*U(1)]; ... ; E[X*U(n)] ]
B = mean(X .* [theOnes U]);

% construct B
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% YOUR CODE HERE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% solve A*Beta = B
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% YOUR CODE HERE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Beta = A\B';

% then compute sigma according to eq. (11) in PA description
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% YOUR CODE HERE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
CovXX = mean(X.*X) - mean(X)^2;

% (I.) Takes the A matrix omitting the first row and last column
%  A = [ E[U(1)*U(1)], E[U(2)*U(1)], ... , E[U(n)*U(1)], ;
%        ...         , ...         , ... , ...         , ;
%        E[U(1)*U(n)], E[U(2)*U(n)], ... , E[U(n)*U(n)]  ]
%
% (II.) Multiplies the last column of A with the first row of A omitting
% the one at the top right corner: 
% [E[U(1)],      E[U(2)],      ... , E[U(n)]]' * [E[U(1)],      E[U(2)],      ... , E[U(n)]] = 
%
% [E[U(1)]*E[U(1)],      E[U(1)]*E[U(2)],      ... , E[U(1)]*E[U(n)];
%  E[U(2)]*E[U(1)],      E[U(2)]*E[U(2)],      ... , E[U(2)]*E[U(n)];
%  ...         	  , 	 ...  		    ,      ... , ...            ;
%  E[U(n)]*E[U(1)],      E[U(n)]*E[U(2)],      ... , E[U(n)]*E[U(n)]];

CovUU = ...
	A(2:end,1:end-1) - ... %(I.)
	A(:,end)(2:end)*A(1,:)(1:end-1);% (II.)
	
sigma = sqrt(CovXX - Beta(1:end-1)'*CovUU*Beta(1:end-1));
