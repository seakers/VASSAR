%% test_oct1.m
parents{1} = PACK_fix(randi(5,[1 10]));parents{1} = [1 2 3 4 2 2 4 3 1 1];
parents{2} = PACK_fix(randi(5,[1 10]));parents{2} = [1 1 1 1 1 1 2 2 2 3];
options.LinearConstr.type = 'unconstrained';
% xoverKids  = PACK_crossover2(parents,options,10,[],[],[]);

mutationChildren = PACK_mutation2(2,options,10,[],[],[],parents',[],[]);