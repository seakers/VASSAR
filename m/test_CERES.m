orbit = get_constellation_orbit(800,'SSO','AM','SSO',1,1);
mission_set{1} = create_test_mission('test',{'CERES','CERES-B'},params.startdate,params.lifetime,orbit);
orbit = get_constellation_orbit(800,'SSO','AM','SSO',1,1);
mission_set{2} = create_test_mission('test',{'CERES-C','MODIS'},params.startdate,params.lifetime,orbit);

[score_vec,panel_scores_mat,data_continuity_score_vec,lists,cost_vec,subobjective_scores,dc_matrices,orbits] = RBES_Evaluate_MissionSet(mission_set);
[combined_score,combined_pan,combined_obj,combined_subobj] = RBES_combine_subobj_scores(subobjective_scores);
score = combined_subobj{1}{6}