function [ret,labels] = num_lowTRL_instr_filter(arch)
% Assign numerical values in increasing order set by labels
params = get_params;
AE = get_AE;
ret= 0;
for i = 1:AE.getLowTRLinstruments.size
    if arch.hasInstrument(AE.getLowTRLinstruments.get(i-1))
        ret = ret + 1;
    end
end
labels = java.util.HashMap;
for i = 0:params.ninstr
    tmp = double(i);
    labels.put(tmp,[num2str(i) ' low TRL instruments']);
end
	
end