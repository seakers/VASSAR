%% test_health_rules.m
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
load_rules_from_excel(r,'C:\Documents and Settings\Dani\My Documents\PhD\research\projects\Rule-based System Architecting\Decadal Objective Rule Definition.xlsx','Health');

%% Facts: Import test measurements
% command = '(batch "C:\\Documents and Settings\\Dani\\My Documents\\NetBeansProjects\\EOLanguage\\src\\facts_test_measurements2.clp")';
% r.eval(command);
r = create_test_facts_from_excel(r,'C:\Documents and Settings\Dani\My Documents\PhD\research\projects\Rule-based System Architecting\Decadal Objective Rule Definition.xlsx','Health');

%% Run jess
r.setResetGlobals(false);    
r.reset;
r.run;

%% Get values of all subobjective variables
[obj_HE1,obj_HE2,obj_HE3,obj_HE4,obj_HE5,obj_HE6] = get_health_objectives_values(r);