function bool = has_instruments_in_orbit(instrs,orb,arch)
    payl= arch.getPayloadInOrbit(orb);
    l1 = length(intersect(instrs,cell(payl)));
    l2 = length(instrs);
    bool = (l1 == l2);  
end