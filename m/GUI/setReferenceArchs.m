function [ref_arch,ref_label]=setReferenceArchs()

global resCol 
AE = get_AE;

%Add empty arch
resCol.pushResult(AE.evaluateArchitecture(rbsa.eoss.ArchitectureGenerator.getInstance.getMinArch,'Slow'));

%Ref arch1
mapping = java.util.HashMap;
mapping.put('LEO-600-polar-NA',{''});
mapping.put('SSO-600-SSO-AM',{'HYSP_TIR'});
mapping.put('SSO-600-SSO-DD',{''});
mapping.put('SSO-800-SSO-PM',{'GACM_VIS','GACM_SWIR'});
mapping.put('SSO-800-SSO-DD',{''});
ref_arch{1} = rbsa.eoss.Architecture(mapping,1);
ref_arch{1}.setEval_mode('DEBUG');
res = AE.evaluateArchitecture(ref_arch{1},'Slow');
resCol.pushResult(res);
ref_label{1} = 'Ref Arch #1';

%Ref arch2
mapping2 = java.util.HashMap;
mapping2.put('LEO-600-polar-NA',{''});
mapping2.put('SSO-600-SSO-AM',{'HYSP_TIR'});
mapping2.put('SSO-600-SSO-DD',{''});
mapping2.put('SSO-800-SSO-PM',{'GACM_VIS','GACM_SWIR','POSTEPS_IRS'});
mapping2.put('SSO-800-SSO-DD',{'DESD_SAR'});
ref_arch{2} = rbsa.eoss.Architecture(mapping2,1);
ref_arch{2}.setEval_mode('DEBUG');
res2 = AE.evaluateArchitecture(ref_arch{2},'Slow');
resCol.pushResult(res2);
ref_label{2} = 'Ref Arch #2';

%Ref arch3
mapping3 = java.util.HashMap;
mapping3.put('LEO-600-polar-NA',{'CLAR_ERB'});
mapping3.put('SSO-600-SSO-AM',{'HYSP_TIR','POSTEPS_IRS'});
mapping3.put('SSO-600-SSO-DD',{''});
mapping3.put('SSO-800-SSO-PM',{'GACM_VIS','GACM_SWIR','POSTEPS_IRS'});
mapping3.put('SSO-800-SSO-DD',{'DESD_SAR'});
ref_arch{3} = rbsa.eoss.Architecture(mapping3,1);
ref_arch{3}.setEval_mode('DEBUG');
res3 = AE.evaluateArchitecture(ref_arch{3},'Slow');
resCol.pushResult(res3);
ref_label{3} = 'Ref Arch #3';

%Ref arch4
mapping4 = java.util.HashMap;
mapping4.put('LEO-600-polar-NA',{'CLAR_ERB','CNES_KaRIN'});
mapping4.put('SSO-600-SSO-AM',{'HYSP_TIR','POSTEPS_IRS'});
mapping4.put('SSO-600-SSO-DD',{'DESD_LID'});
mapping4.put('SSO-800-SSO-PM',{'GACM_VIS','GACM_SWIR','POSTEPS_IRS'});
mapping4.put('SSO-800-SSO-DD',{'DESD_SAR'});
ref_arch{4} = rbsa.eoss.Architecture(mapping4,1);
ref_arch{4}.setEval_mode('DEBUG');
res4 = AE.evaluateArchitecture(ref_arch{4},'Slow');
resCol.pushResult(res4);
ref_label{4} = 'Ref Arch #4';

%Ref arch5
mapping5 = java.util.HashMap;
mapping5.put('LEO-600-polar-NA',{'CLAR_ERB','CNES_KaRIN','ACE_POL','ACE_ORCA'});
mapping5.put('SSO-600-SSO-AM',{'HYSP_TIR','POSTEPS_IRS','ACE_LID'});
mapping5.put('SSO-600-SSO-DD',{'DESD_LID'});
mapping5.put('SSO-800-SSO-PM',{'GACM_VIS','GACM_SWIR','POSTEPS_IRS'});
mapping5.put('SSO-800-SSO-DD',{'DESD_SAR'});
ref_arch{5} = rbsa.eoss.Architecture(mapping5,1);
ref_arch{5}.setEval_mode('DEBUG');
res5 = AE.evaluateArchitecture(ref_arch{5},'Slow');
resCol.pushResult(res5);
ref_label{5} = 'Ref Arch #5';


end