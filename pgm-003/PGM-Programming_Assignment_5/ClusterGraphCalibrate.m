% CLUSTERGRAPHCALIBRATE Loopy belief propagation for cluster graph calibration.
%   P = CLUSTERGRAPHCALIBRATE(P, useSmart) calibrates a given cluster graph, G,
%   and set of of factors, F. The function returns the final potentials for
%   each cluster. 
%   The cluster graph data structure has the following fields:
%   - .clusterList: a list of the cluster beliefs in this graph. These entries
%                   have the following subfields:
%     - .var:  indices of variables in the specified cluster
%     - .card: cardinality of variables in the specified cluster
%     - .val:  the cluster's beliefs about these variables
%   - .edges: A cluster adjacency matrix where edges(i,j)=1 implies clusters i
%             and j share an edge.
%  
%   UseSmart is an indicator variable that tells us whether to use the Naive or Smart
%   implementation of GetNextClusters for our message ordering
%
%   See also FACTORPRODUCT, FACTORMARGINALIZATION
%
% Copyright (C) Daphne Koller, Stanford University, 2012

function [P MESSAGES] = ClusterGraphCalibrate(P,useSmartMP)

if(~exist('useSmartMP','var'))
  useSmartMP = 0;
end

N = length(P.clusterList);

MESSAGES = repmat(struct('var', [], 'card', [], 'val', []), N, N);
[edgeFromIndx, edgeToIndx] = find(P.edges ~= 0);

for m = 1:length(edgeFromIndx),
    i = edgeFromIndx(m);
    j = edgeToIndx(m);

    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % YOUR CODE HERE
    %
    %
    %
    % Set the initial message values
    % MESSAGES(i,j) should be set to the initial value for the
    % message from cluster i to cluster j
    %
    % The matlab/octave functions 'intersect' and 'find' may
    % be useful here (for making your code faster)
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    clusterI = P.clusterList(i);
	clusterJ = P.clusterList(j);
	
	[vars, clusterIIdxs, _] = intersect(clusterI.var, clusterJ.var);
	MESSAGES(i,j).var = vars;
	MESSAGES(i,j).card = clusterI.card(clusterIIdxs);
	valCount = prod(MESSAGES(i,j).card);
	MESSAGES(i,j).val = ones(1, valCount);
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
end;



% perform loopy belief propagation
tic;
iteration = 0;

lastMESSAGES = MESSAGES;
%InitPlot();
while (1),
    iteration = iteration + 1;
    [i, j] = GetNextClusters(P, MESSAGES,lastMESSAGES, iteration, useSmartMP); 
    prevMessage = MESSAGES(i,j);

    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % YOUR CODE HERE
    % We have already selected a message to pass, \delta_ij.
    % Compute the message from clique i to clique j and put it
    % in MESSAGES(i,j)
    % Finally, normalize the message to prevent overflow
    %
    % The function 'setdiff' may be useful to help you
    % obtain some speedup in this function
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
	clusterI = P.clusterList(i);
	
	msgSourceClusters = setdiff(edgeFromIndx(find(edgeToIndx == i)), j); %previous messages to clique i, except j the target clique we are sending messages to
	
	belief = clusterI;
	for k=1:length(msgSourceClusters),
		belief = FactorProduct(belief, MESSAGES(msgSourceClusters(k), i));
	end;
	message = FactorMarginalization(belief, setdiff(clusterI.var, prevMessage.var)); %sum out non message variables
	message.val = message.val ./ sum(message.val);
	
	MESSAGES(i,j) = message;

    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    if(useSmartMP==1)
      lastMESSAGES(i,j)=prevMessage;
    end
    
    % Check for convergence every m iterations
    if mod(iteration, length(edgeFromIndx)) == 0
		%PlotResiduals(MESSAGES, lastMESSAGES, floor(iteration/length(edgeFromIndx)), {[19 3], [15 40], [17 2]});%
        if (CheckConvergence(MESSAGES, lastMESSAGES))
            break;
        end
        %disp(['LBP Messages Passed: ', int2str(iteration), '...']);
        if(useSmartMP~=1)
          lastMESSAGES=MESSAGES;
        end
    end
    
	if (iteration > 100000),
		break;
	end;
end;
toc;
disp(['Total number of messages passed: ', num2str(iteration)]);


% Compute final potentials and place them in P
for m = 1:length(edgeFromIndx),
    j = edgeFromIndx(m);
    i = edgeToIndx(m);
    P.clusterList(i) = FactorProduct(P.clusterList(i), MESSAGES(j, i));
end
end;

% Get the max difference between the marginal entries of 2 messages -------
function delta = MessageDelta(Mes1, Mes2)
	delta = max(abs(Mes1.val - Mes2.val));
	return;
end;

function InitPlot()
	close all;
	if (~ishold),
		hold on;
	end;
	ylim([0 1000*1.0e-6]);
	%xlim([0 4000]);
end;

function PlotResiduals(actMsgs, prevMsgs, iteration, edges)
	for i=1:length(edges),
		edge = edges{i};
		delta = MessageDelta(actMsgs(edge(1),edge(2)), prevMsgs(edge(1),edge(2)));
		plot(iteration, delta, strcat("@", num2str(i)));
	end
end;
%ClusterGraphCalibrate(CreateClusterGraph(factors,[]));
%load exampleIOPA5
%[n1,f1] = ConstructToyNetwork(1,.2)
%[n2,f2] = ConstructToyNetwork(.2,1);
%[n3,f3] = ConstructToyNetwork(.5,.5);