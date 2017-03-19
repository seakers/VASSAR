function excel_data = get_ecv_scores(archs)
%% This function writes in an excel-like spreadsheet the ecv (objective) scores for the results provided (resu is a cell array of results)
    excel_data = zeros(length(archs),50);
    params = get_params;
    AE = get_AE;
    for i = 1:length(archs)
        arch = archs{i};
        arch.setEval_mode('DEBUG');
        resu = AE.evaluateArchitecture(arch,'Slow');
        score_panels = zeros(1,3);
        ecv = 1;
        for p = 1:params.subobjectives.size
            panel = params.subobjectives.get(p-1);
            obj_weights = params.obj_weights.get(p-1);
            for o = 1:panel.size
                obj = panel.get(o-1);
                score_obj = 0;
                for s = 1:obj.size
                    subobj = obj.get(s-1);
                    score_obj = score_obj + params.subobj_weights_map.get(subobj)*resu.getSubobjective_scores2.get(subobj);
                end
                excel_data(i,ecv) = score_obj;
                ecv = ecv + 1;
                score_panels(p) = score_panels(p) + obj_weights.get(o-1)*score_obj;
            end
        end
        fprintf('Ref arch %d: cost = %.0f [%.0f - %.0f] sc = %.3f [%.3f - %.3f], [ATM,OCE,TER] = %.3f, %.3f, %.3f\n',i,resu.getCost,resu.getFuzzy_cost.getInterv.getMin,resu.getFuzzy_cost.getInterv.getMax,resu.getScience,resu.getFuzzy_science.getInterv.getMin,resu.getFuzzy_science.getInterv.getMax,score_panels(1),score_panels(2),score_panels(3));
    end
end