%% test_weather_rules.m
[r,keys_to_attribs] = init_KBEOSS2();

%% Import global variables for objective definition
r = load_globals(r);

%% Create Measuremenet template
r = load_templates(r,keys_to_attribs);

%% User functions
r = load_functions(r);

%% Import rules for subobjectives (from excel?)
% panels = [0 1];% climate weather
% r = load_rules(r,panels);
load_rules_from_excel(r,'C:\Documents and Settings\Dani\My Documents\PhD\research\projects\Rule-based System Architecting\Decadal Objective Rule Definition.xlsx','Weather');

%% Facts: Import test measurements
% command = '(batch "C:\\Documents and Settings\\Dani\\My Documents\\NetBeansProjects\\EOLanguage\\src\\facts_test_measurements2.clp")';
% r.eval(command);
r = create_test_facts_from_excel(r,'C:\Documents and Settings\Dani\My Documents\PhD\research\projects\Rule-based System Architecting\Decadal Objective Rule Definition.xlsx','Weather');

%% Run jess
r.setResetGlobals(false);    
r.reset;
r.run;

%% Get values of all subobjective variables
[obj_WE1,obj_WE2,obj_WE3,obj_WE4,obj_WE5,obj_WE6,obj_WE7] = get_weather_objectives_values(r);