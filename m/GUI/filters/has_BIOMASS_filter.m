function [ret,labels] = has_BIOMASS_filter(arch)
% Assign numerical values in increasing order set by labels
params = get_params;
labels = java.util.HashMap;
labels.put(0,'no BIOMASS');
labels.put(1,'has BIOMASS');
for i = 1:length(params.orbit_list)
    tmp = arch.getPayloadInOrbit(params.orbit_list(i));
    for j = 1:tmp.length
        if strcmp(tmp(j),'BIOMASS')
            ret = 1;
            return;
        end
    end
end
ret = 0;
end