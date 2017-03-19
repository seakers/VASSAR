function ret1 = format_arch_to_table( arch )
    % Initialize the variables
%     global folder
    params = get_params();
    ret1 = {};
    orbits = params.orbit_list;
%     ret1(1,1:2) = {'Num sats per plane' num2str(arch.getNsats())};
    ret1{1,1} = ['Architecture ' char(arch.getId)];
    ret1{2,1} = ['Benefit = ' num2str(arch.getResult.getScience)];
    ret1{3,1} = ['Fuzzy Benefit = ' char(arch.getResult.getFuzzy_science)];
    ret1{4,1} = ['Cost = ' num2str(arch.getResult.getCost)];
    ret1{5,1} = ['Fuzzy Cost = ' char(arch.getResult.getFuzzy_cost)];
    ret1(6,1:8) = {'Orbit' 'Num sats in orbit' 'Instrument 1' 'Instrument 2' 'Instrument 3' 'Instrument 4' 'Instrument 5' 'Instrument 6'};
    for o = 1:length(orbits)
        ret1{o+6,1} = char(params.orbit_list(o));
        ret1{o+6,2} = num2str(arch.getNsats());
        tmp= arch.getPayloadInOrbit(params.orbit_list(o));
        for j = 1:length(tmp)
            ret1{o+6,j+2} = char(tmp(j));
        end
        for j = length(tmp)+1:length(tmp)+10
            ret1{o+6,j+2} = '';
        end
    end
end



