function data = create_cost_table

    global zeResult;
    j = global_jess_engine();
    c = j.getGlobalContext();
    
    data = cell(100,10);

    % Cost model
    % [num,txt,raw] = xlsread(params.master_xls,'Output');
    % headers = txt(:,1);
    % Cost model
    % Ground segment cost
    data(1,2:4) = {'Total', 'Non-recurring', 'Recurring'};

    missions = zeResult.getCost_facts;
    n = 2;
    for i = 1:missions.size
        mission = missions.get(i-1);
        data(n,1:2) = {char(mission.getSlotValue('orbit-string').stringValue(c)) '------------------------------------------------------'};n=n+1;
        data(n,1:4) = {'payload-cost#' mission.getSlotValue('payload-cost#').floatValue(c)/1000 mission.getSlotValue('payload-non-recurring-cost#').floatValue(c)/1000 mission.getSlotValue('payload-recurring-cost#').floatValue(c)/1000};n=n+1;
        data(n,1:4) = {'bus-cost#' mission.getSlotValue('bus-cost#').floatValue(c)/1000 mission.getSlotValue('bus-non-recurring-cost#').floatValue(c)/1000 mission.getSlotValue('bus-recurring-cost#').floatValue(c)/1000};n=n+1;
        data(n,1:4) = {'IAT-cost#' mission.getSlotValue('IAT-cost#').floatValue(c)/1000 mission.getSlotValue('IAT-non-recurring-cost#').floatValue(c)/1000 mission.getSlotValue('IAT-recurring-cost#').floatValue(c)/1000};n=n+1;
        data(n,1:4) = {'launch-cost#' mission.getSlotValue('launch-cost#').floatValue(c) mission.getSlotValue('num-launches').floatValue(c) char(mission.getSlotValue('launch-vehicle').stringValue(c))};n=n+1;
        data(n,1:4) = {'program-cost#' mission.getSlotValue('program-cost#').floatValue(c)/1000 mission.getSlotValue('program-non-recurring-cost#').floatValue(c)/1000 mission.getSlotValue('program-recurring-cost#').floatValue(c)/1000};n=n+1;
        data(n,1:4) = {'operations-cost#' mission.getSlotValue('operations-cost#').floatValue(c)/1000 0 0};n=n+1;
        data(n,1:4) = {'Total mission' mission.getSlotValue('lifecycle-cost#').floatValue(c) mission.getSlotValue('mission-non-recurring-cost#').floatValue(c) mission.getSlotValue('mission-recurring-cost#').floatValue(c)};n=n+1;
        n=n+1;
    end
   
    
end