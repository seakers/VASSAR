%% test_script.m
RBES_Init_Params_Iridium
[r,params] = RBES_Init_WithRules(params);
mission = create_test_mission({'BIOMASS','LORENTZ_ERB','CTECS','MICROMAS'},2011,8);
% 
r.reset;
%% Assert Mission
n = length(mission.instrument_list);
instr_list_str = [];
for i = 1:n
instr_list_str = [instr_list_str ' ' mission.instrument_list{i}];
end
if ~ischar(mission.orbit.inclination)
incl = num2str(mission.orbit.inclination);
else
incl = char(mission.orbit.inclination);
end

%% Test Iridium
Vraan = [0 180];
Vano = [0 180];
for i = 1:2
    for j = 1:2
        raan = Vraan(i);
        ano = Vano(j);
        call = ['(assert (MANIFEST::Mission (Name ' char(mission.name) num2str(i) num2str(j) ')' ...
        ' (orbit-altitude# ' num2str(mission.orbit.altitude) ')' ...
        ' (orbit-inclination ' incl ')' ...
        ' (orbit-RAAN ' num2str(raan) ')' ...
        ' (orbit-anomaly# ' num2str(ano) ')' ...
        ' (instruments ' instr_list_str ')' ...
        ' (lifetime ' num2str(mission.lifetime) ')' ...
        ' (launch-date ' num2str(mission.launch_date) ')' ...
        '))'];
        r.eval(call);
    end
end

r.eval('(focus MANIFEST)');
r.run;
%% Focus CAPABILITIES and run to get REQUIREMENTS::measurements
r.eval('(focus CAPABILITIES)');
r.run;

%% Load ASSIMILATION rules, focus, run, and compute assimilated revisit times
[r,params] = load_assimilation_rules(r,params); % can't load them before because they fire too soon
r.eval('(focus ASSIMILATION)');
r.run
map = r.eval('(fetch MAP)').javaObjectValue(r.getGlobalContext());% this is a map with key parameter, object = array of lists (fov,h,inc,raan,ano)
meas_array = map.keySet.toArray;
num_measurements = length(meas_array);
for i = 1:num_measurements
    param = meas_array(i);
    list_of_orbits = map.get(param).toArray;%  array where each element is (fov,h,inc,raan,ano)
    [num_planes,num_sat_per_planes,h,inc,fov] = get_nplanes_and_nsats(list_of_orbits);
    call = ['(defrule ASSIMILATION::compute-aggregated-revisit-time-' num2str(i) ' ' ...
        '?m <- (REQUIREMENTS::Measurement (Parameter "' param '"))' ...
        '(DATABASE::Revisit-time-of (mission-architecture constellation) (num-of-planes# ' num2str(num_planes) ') ' ...
        '(num-of-sats-per-plane# ' num2str(num_sat_per_planes) ') ' ...
        '(orbit-altitude# ' num2str(h) ') ' ...
        '(orbit-inclination ' num2str(inc) ') ' ...
        '(instrument-field-of-view# ' num2str(fov) ') ' ...
        '(avg-revisit-time-global# ?glob) (avg-revisit-time-tropics# ?trop) (avg-revisit-time-northern-hemisphere# ?nh)' ...
        '(avg-revisit-time-southern-hemisphere# ?sh) (avg-revisit-time-cold-regions# ?cold) (avg-revisit-time-US# ?us))' ...
        ' => ' ...
        '(modify ?m (avg-revisit-time-global# ?glob) (avg-revisit-time-tropics# ?trop) (avg-revisit-time-northern-hemisphere# ?nh)' ...
        '(avg-revisit-time-southern-hemisphere# ?sh) (avg-revisit-time-cold-regions# ?cold) (avg-revisit-time-US# ?us))' ...
        ')'];
    r.eval(call);
        
end
% r.eval('(watch all)');
r.eval('(focus ASSIMILATION)');
r.run
% t.get('1.2.1 Atmospheric temperature fields')
% l = ans
% qq = l.toArray


%% Focus FUZZY and run to transform quantitative to qualitative attributes
r.eval('(focus FUZZY)');
r.run
