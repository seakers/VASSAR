function ret1 = create_satisfaction_table
 
    global zeResult;
%     global params
%     global AE
    params = get_params;
    AE = get_AE;
    exp = zeResult.getExplanations;
    subobj_scores = zeResult.getSubobjective_scores2;
    it = exp.keySet.iterator;
    ret1 = {'Subobjective' 'Parameter' 'Score' 'Taken by'};
    while it.hasNext
        subobj = it.next;
        score = subobj_scores.get(subobj);
        [ret,ret2] = capa_vs_req(zeResult,subobj,AE,params);
        ret1 = [ret1;ret2];
    end
end