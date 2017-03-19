function excel_data = get_ecv_scores_for_chosen_archs()
    global chosen_archs
    excel_data = zeros(5,50);
    resu = cell(5,1);
    AE = get_AE;
    params = get_params;
    for i = 1:length(chosen_archs)
        resu{i} = AE.evaluateArchitecture(chosen_archs{i},'Slow');
        ecv = 1;
        score_panels = zeros(1,3);
        for p = 1:params.subobjectives.size
            panel = params.subobjectives.get(p-1);
            obj_weights = params.obj_weights.get(p-1);
            for o = 1:panel.size
                obj = panel.get(o-1);
                score_obj = 0;
                for s = 1:obj.size
                    subobj = obj.get(s-1);
                    score_obj = score_obj + params.subobj_weights_map.get(subobj)*resu{i}.getSubobjective_scores2.get(subobj);
                end
                excel_data(i,ecv) = score_obj;
                ecv = ecv + 1;
                score_panels(p) = score_panels(p) + obj_weights.get(o-1)*score_obj;
            end    
        end
        fprintf('Ref arch %d: cost = %.0f [%.0f - %.0f] sc = %.3f [%.3f - %.3f], [ATM,OCE,TER] = %.3f, %.3f, %.3f\n',i,resu{i}.getCost,resu{i}.getFuzzy_cost.getInterv.getMin,resu{i}.getFuzzy_cost.getInterv.getMax,resu{i}.getScience,resu{i}.getFuzzy_science.getInterv.getMin,resu{i}.getFuzzy_science.getInterv.getMax,score_panels(1),score_panels(2),score_panels(3));
    end
end