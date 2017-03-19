function ret = tradespace_data_mining(resCol)
%% Prepare
%     javaaddpath('.\java\jess.jar');
%     javaaddpath('.\java\jxl.jar');
%     javaaddpath('./java/combinatoricslib-2.0.jar');
%     javaaddpath('./java/commons-lang3-3.1.jar');
%     javaaddpath('./java/matlabcontrol-4.0.0.jar');
%     %javaaddpath( '.\java\RBES_Demo_May2014.jar' );
%     javaaddpath( '.\java\RBES_EOSS.jar' );
%     import rbsa.eoss.*
%     import rbsa.eoss.local.*
%     import java.io.*;
%     import jess.*;
    params = get_params;
%     AE = get_AE;
%     RM = rbsa.eoss.ResultManager.getInstance;
%     resCol = RM.loadResultCollectionFromFile('C:\Users\Ana-Dani\Documents\GitHub\RBES_EOSS\results\2014-09-11_14-39-22_test.rs');
    results = resCol.getResults;
    narch = results.size;
    archs = cell(narch,1);

    %% Find pareto archs
    xvals = zeros(narch,1);
    yvals = zeros(narch,1);
    for i = 1:narch
        archs{i} = results.get(i-1).getArch;
        xvals(i) = results.get(i-1).getScience;
        yvals(i) = results.get(i-1).getCost;
    end
    i_pareto = paretofront([-xvals yvals]);
%     x_vals2 = xvals(i_pareto);
%     y_vals2 = yvals(i_pareto);
%     [~, I] = sort(x_vals2);
%     narch_front = length(I);
    pareto_archs = archs(i_pareto);
    narch_front = length(pareto_archs);
%     pareto_archs = pareto_archs (I);
    
    %% Create feature predicate functions
    nfeatures = params.norb*(params.ninstr + nchoosek(params.ninstr,2));
    feature_funcs = cell(nfeatures,1); 
    n = 1;
    
    % Single instrument-orbit assignments
    for o = 1:params.norb
        orb = char(params.orbit_list(o));
        for i = 1:params.ninstr
            instr = char(params.instrument_list(i));
            eval(['feature_funcs{n} = @(arch)has_instruments_in_orbit({''' instr '''}, ''' orb ''' ,arch);']);
            n = n + 1;
        end
    end
    
    % Pairs of instruments - orbit assignments
    for o = 1:params.norb
        orb = char(params.orbit_list(o));
        for i = 1:params.ninstr-1
            instr = char(params.instrument_list(i));
            for j = i+1:params.ninstr
                instr2 = char(params.instrument_list(j));
                eval(['feature_funcs{n} = @(arch)has_instruments_in_orbit({''' instr ''',''' instr2 '''}, ''' orb ''' ,arch);']);
                n = n + 1;
            end
        end
    end
    %% Check all features against all archs
    feature_counter = zeros(nfeatures,1);
    for i = 1:narch
        arch = archs{i};
        for f = 1:nfeatures
            feat = feature_funcs{f};
            fprintf('Checking feature %s on arch %s\n',func2str(feat),char(arch.toString));
            bool = feat(arch);
            feature_counter(f) = feature_counter(f) + bool;
        end
    end
    
    %% Check all features against all Pareto archs
    feature_counter_pareto = zeros(nfeatures,1);
    for i = 1:narch_front
        arch = pareto_archs{i};
        for f = 1:nfeatures
            feat = feature_funcs{f};
            bool = feat(arch);
            feature_counter_pareto(f) = feature_counter_pareto(f) + bool;
        end
    end
    
    %% Assemble outputs
    ret.archs = archs;
    ret.features = cellfun(@func2str,feature_funcs,'UniformOutput',false);
    ret.narch = narch;
    ret.narch_front = narch_front;
    ret.feature_counter = feature_counter;
    ret.feature_counter_pareto = feature_counter_pareto;
    ret.norm_feature_counter = feature_counter/narch;
    ret.norm_feature_counter_pareto = feature_counter_pareto/narch_front;
    [sorted_features_by_lastpop,order_by_lastpop] = sort(ret.norm_feature_counter,'descend');
    ret.sorted_features_by_lastpop = ret.features(order_by_lastpop);
    [sorted_features_by_pareto,order_by_pareto] = sort(ret.norm_feature_counter_pareto,'descend');
    ret.sorted_features_by_pareto = ret.features(order_by_pareto);
    
    %% Plots
    figure;bar(sorted_features_by_lastpop);
    figure;bar(sorted_features_by_pareto);
end

