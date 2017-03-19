%% test_performance_RBES2.m
r = global_jess_engine;
%% define templates
jess deftemplate value (slot of) (multislot is);
jess deftemplate system (slot id) (slot parent);
jess deftemplate att-1 (slot of) (slot is);