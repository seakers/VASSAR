function [ret1,ret2] = get_facts_values( facts, values )

    % facts = ArrayList<Fact>
    % values = {'value1','value2',...}

    ret1 = cell( length(values), facts.size );
    ret2 = cell( length(values), facts.size );
    for i = 0:facts.size-1
        for j = 1:length(values)
            ret1{j,i+1} = jess_str_value( facts.get(i).getSlotValue( values{j} ) );
            ret2{j,i+1} = jess_value( facts.get(i).getSlotValue( values{j} ) );
        end
    end
%     ret2(1,:) = [];
%     ret2 = cell2mat(ret2);
end

