facts = r.listFacts(); % iterator
map = java.util.HashMap;
while facts.hasNext()
    f = facts.next();
    if ~strcmp(f.getDeftemplate,'[deftemplate ORBIT-SELECTION::orbit]')
        continue
    end
    miss = char(f.getSlotValue('in-mission').stringValue(r.getGlobalContext()));
    var = char(f.getSlotValue('penalty-var').stringValue(r.getGlobalContext()));
    %str = char(f.getSlotValue('of-instrument').stringValue(r.getGlobalContext()));
    penalty = r.eval(var).floatValue(r.getGlobalContext());
    if map.containsKey(miss)
        pen = map.get(miss);
        map.put(miss,pen+penalty);
    else
        map.put(miss,penalty);
    end
end

tmp = map.entrySet.iterator;
min_penalty = Inf;
best_orbit = java.util.ArrayList;
while(tmp.hasNext())
    entry = tmp.next();
    if entry.getValue < min_penalty
        best_orbit = java.util.ArrayList;
        best_orbit.add(entry.getKey);
        min_penalty = entry.getValue;
    elseif entry.getValue == min_penalty
        best_orbit.add(entry.getKey);
    end
end