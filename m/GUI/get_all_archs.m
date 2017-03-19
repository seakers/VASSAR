function [archs] = get_all_archs
    global resCol
    results = resCol.getResults;
    results.get(0).getArch;
    narch = results.size;
    archs = cell(narch,1);
    
    for i = 1:narch
        archs{i} = results.get(i-1).getArch;
        
    end
end   
