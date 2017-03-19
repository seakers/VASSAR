% RBES_Init_Params_EOS;
% [r,params] = RBES_Init_WithRules(params);
% sciences = zeros(200,1);
% archs = cell(200,1);
% costs = zeros(200,1);

for i = 1:200
    archs{i} = PACK_fix(randi(5,[1 16]));
    arch.packaging = archs{i};
    fprintf('Evaluating arch %d of 200: ',i);
    fprintf('%d-',archs{i});fprintf('...');
    [sciences(i),costs(i),~] = PACK_evaluate_architecture(r,params,arch);
    fprintf('science = %f cost = %f\n',sciences(i),costs(i)/1000);
end
save test_memory archs sciences costs