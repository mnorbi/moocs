%CLIQUETREECALIBRATE Performs sum-product or max-product algorithm for 
%clique tree calibration.

%   P = CLIQUETREECALIBRATE(P, isMax) calibrates a given clique tree, P 
%   according to the value of isMax flag. If isMax is 1, it uses max-sum
%   message passing, otherwise uses sum-product. This function 
%   returns the clique tree where the .val for each clique in .cliqueList
%   is set to the final calibrated potentials.
%
% Copyright (C) Daphne Koller, Stanford University, 2012

function P = CliqueTreeCalibrate(P, isMax)


% Number of cliques in the tree.
N = length(P.cliqueList);

% Setting up the messages that will be passed.
% MESSAGES(i,j) represents the message going from clique i to clique j. 
MESSAGES = repmat(struct('var', [], 'card', [], 'val', []), N, N);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% We have split the coding part for this function in two chunks with
% specific comments. This will make implementation much easier.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% YOUR CODE HERE
% While there are ready cliques to pass messages between, keep passing
% messages. Use GetNextCliques to find cliques to pass messages between.
% Once you have clique i that is ready to send message to clique
% j, compute the message and put it in MESSAGES(i,j).
% Remember that you only need an upward pass and a downward pass.
%
 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
if isMax,
	for x = 1:N,
		P.cliqueList(x).val = log(P.cliqueList(x).val);
	end;
end;

[i j] = GetNextCliques(P, MESSAGES);
while find([i j]), %still have unsent message
	message = P.cliqueList(i);
	
	%compute message to be sent
	adjacent = setdiff(find(P.edges(:, i) == 1), j); %previous messages to clique i, except j the target clique we are sending messages to
	for k=1:length(adjacent),
		if isMax,
			message = FactorSum(message, MESSAGES(adjacent(k), i));
		else
			message = FactorProduct(message, MESSAGES(adjacent(k), i));
		end;
	end;
	
	if isMax,
		message = FactorMaxMarginalization(message, setdiff(P.cliqueList(i).var, P.cliqueList(j).var));
	else
		message = FactorMarginalization(message, setdiff(P.cliqueList(i).var, P.cliqueList(j).var)); %sum out non common variables from source clique
		
		message.val = message.val ./ sum(message.val);%normalize
	end;
	
	MESSAGES(i, j) = message; %store message
	
	[i j] = GetNextCliques(P, MESSAGES); %iterate to the next node pairs
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% YOUR CODE HERE
%
% Now the clique tree has been calibrated. 
% Compute the final potentials for the cliques and place them in P.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
for x = 1:length(P.cliqueList),
	inboundIMsgs = MESSAGES(find(P.edges(:,x) == 1), x);
	for k=1:length(inboundIMsgs),
		if isMax,
			P.cliqueList(x) = FactorSum(P.cliqueList(x), inboundIMsgs(k));
		else
			P.cliqueList(x) = FactorProduct(P.cliqueList(x), inboundIMsgs(k));
		end;
	end;
end;
return
