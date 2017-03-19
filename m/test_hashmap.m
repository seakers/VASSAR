%% test_hashmap.m
clear all
N = 10000;
NI = 39;
hm = java.util.HashMap;
hm2= java.util.HashMap;
for i = 1:N
    arch = randi(NI,[1 NI]);
    hm.put(i,arch);
    hm2.put(arch,rand(1,4));
end
save test_hashmap hm
save test_hashmap2 hm2