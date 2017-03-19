function [ret,labels] = num_sats_filter(arch)
% Assign numerical values in increasing order set by labels
params = get_params;
ret = arch.getNsats + 0.0;
labels = java.util.HashMap;
for i = 1:length(params.nsats)
    tmp = double(params.nsats(i));
    labels.put(tmp,[num2str(params.nsats(i)) ' sats']);
end
% labels = cellfun(@num2str,num2cell(1:1:params.nsats(end)),'UniformOutput', false);
	
end