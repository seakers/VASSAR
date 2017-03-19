function resu = get_scores_singlearchs()
    params = get_params;
    AE = get_AE;
    resu = cell(params.ninstr,1);
    for i = 1:params.ninstr
        resu{i} = AE.evaluateArchitecture(arch,'Slow');
    end
end