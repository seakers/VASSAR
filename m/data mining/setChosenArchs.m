function [chosen_archs]=setChosenArchs()

% global resCol 
% AE = get_AE;

%Chosen arch1
mapping = java.util.HashMap;
mapping.put('LEO-600-polar-NA',{'HYSP_TIR','DESD_SAR'});
mapping.put('SSO-600-SSO-AM',{'ACE_ORCA','GACM_VIS','POSTEPS_IRS'});
mapping.put('SSO-600-SSO-DD',{'DESD_LID','CNES_KaRIN'});
mapping.put('SSO-800-SSO-PM',{'GACM_VIS','GACM_SWIR','HYSP_TIR','POSTEPS_IRS'});
mapping.put('SSO-800-SSO-DD',{'HYSP_TIR'});
chosen_archs{1} = rbsa.eoss.Architecture(mapping,1);
chosen_archs{1}.setEval_mode('DEBUG');
% res = AE.evaluateArchitecture(chosen_archs{1},'Slow');
% resCol.pushResult(res);
% Chosen_label{1} = 'Chosen Arch #1';

%Chosen arch2
mapping2 = java.util.HashMap;
mapping2.put('LEO-600-polar-NA',{'HYSP_TIR','DESD_SAR'});
mapping2.put('SSO-600-SSO-AM',{'ACE_ORCA','GACM_VIS','POSTEPS_IRS'});
mapping2.put('SSO-600-SSO-DD',{'CNES_KaRIN'});
mapping2.put('SSO-800-SSO-PM',{'HYSP_TIR','POSTEPS_IRS'});
mapping2.put('SSO-800-SSO-DD',{'HYSP_TIR'});
chosen_archs{2} = rbsa.eoss.Architecture(mapping2,1);
chosen_archs{2}.setEval_mode('DEBUG');
% res2 = AE.evaluateArchitecture(chosen_archs{2},'Slow');
% resCol.pushResult(res2);
% Chosen_label{2} = 'Chosen Arch #2';

%Chosen arch3
mapping3 = java.util.HashMap;
mapping3.put('LEO-600-polar-NA',{'ACE_POL','HYSP_TIR'});
mapping3.put('SSO-600-SSO-AM',{'ACE_ORCA','POSTEPS_IRS','DESD_LID','GACM_VIS'});
mapping3.put('SSO-600-SSO-DD',{});
mapping3.put('SSO-800-SSO-PM',{'GACM_VIS','CNES_KaRIN','POSTEPS_IRS'});
mapping3.put('SSO-800-SSO-DD',{});
chosen_archs{3} = rbsa.eoss.Architecture(mapping3,1);
chosen_archs{3}.setEval_mode('DEBUG');
% res3 = AE.evaluateArchitecture(chosen_archs{3},'Slow');
% resCol.pushResult(res3);
% Chosen_label{3} = 'Chosen Arch #3';



end