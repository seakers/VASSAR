orbit = get_constellation_orbit(800,'SSO','AM','SSO',1,1);
mission_set{1} = create_test_mission('test',{'OMI','MODIS-B'},params.startdate,params.lifetime,orbit);
[scor,panel_score,objective_scor,subobjective_scor2,data_continuity_score,data_continuity_matrix,cos] = RBES_Evaluate_Mission(mission_set{1});
% GHG = subobjective_scor2{3}{1}
% OZO1= subobjective_scor2{6}{1}
% OZO2= subobjective_scor2{6}{2}


orbit = get_constellation_orbit(800,'SSO','PM','SSO',1,1);
mission_set{2} = create_test_mission('test',{'MLS','TES','MODIS'},params.startdate,params.lifetime,orbit);
[scor,panel_score,objective_scor,subobjective_scor2,data_continuity_score,data_continuity_matrix,cos] = RBES_Evaluate_Mission(mission_set{2});
% GHG = subobjective_scor2{3}{1}
% OZO1= subobjective_scor2{6}{1}
% OZO2= subobjective_scor2{6}{2}

[score_vec,panel_scores_mat,data_continuity_score_vec,lists,cost_vec,subobjective_scores,dc_matrices,orbits] = RBES_Evaluate_MissionSet(mission_set);
[combined_score,combined_pan,combined_obj,combined_subobj] = RBES_combine_subobj_scores(subobjective_scores);
GHG = combined_subobj{3}{1}
OZO1= combined_subobj{6}{1}
OZO2= combined_subobj{6}{2}

orbit = get_constellation_orbit(800,'SSO','PM','SSO',1,1);
mission_set{2} = create_test_mission('test',{'MLS','TES','MODIS','OMI'},params.startdate,params.lifetime,orbit);
[scor,panel_score,objective_scor,subobjective_scor2,data_continuity_score,data_continuity_matrix,cos] = RBES_Evaluate_Mission(mission_set{2});
GHG = subobjective_scor2{3}{1}
OZO1= subobjective_scor2{6}{1}
OZO2= subobjective_scor2{6}{2}
