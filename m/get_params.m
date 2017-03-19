function params = get_params
    persistent parameters

    if isempty(parameters) 
        parameters = rbsa.eoss.local.Params(pwd,'FUZZY-ATTRIBUTES','test','normal','');
    end
    params = parameters;
end
