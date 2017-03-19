function CSER_load_perf_data()

path = 'C:\Users\Nozomi\Documents\CSER_2015';
% path = 'C:\Users\SEAK1\Nozomi\CSER_2015';
cd(path);

javaaddpath('.\java\jess.jar');
javaaddpath('.\java\jxl.jar');
javaaddpath('./java/combinatoricslib-2.0.jar');
javaaddpath('./java/commons-lang3-3.1.jar');
javaaddpath('./java/matlabcontrol-4.0.0.jar');
javaaddpath( '.\java\CSER2015.jar' );
import rbsa.eoss.*
import rbsa.eoss.local.*
import madkitdemo3.*
import agentIterfaces.*
import java.io.*;

[AavgDist,AlowCostArch] = loadSearchPerf('Pick ATEAMS no human file');
[AHavgDist,AHlowCostArch] = loadSearchPerf('Pick ATEAMS with human file');
[DavgDist,DlowCostArch] = loadSearchPerf('Pick DMAB no human file');
[DHavgDist,DHlowCostArch] = loadSearchPerf('Pick DMAB with human file');

Alabel = cell(length(AavgDist),1); % cell array
AHlabel = cell(length(AHavgDist),1); % cell array
Dlabel = cell(length(DavgDist),1); % cell array
DHlabel = cell(length(DHavgDist),1); % cell array
ia = 1>0; % vector with logicals
a = 'Asynchronous: Computer-Only'; % string
ah = 'Asynchronous: Computer-Human';
d = 'DMAB: Computer-Only';
dh = 'DMAB: Computer-Human';

%finding the final avg crowding dist, cost and science of cheapest max sci
[AfinalDist,AfinalCost,AfinalSci] = get_final_index(AavgDist, AlowCostArch);
[AHfinalDist,AHfinalCost,AHfinalSci] = get_final_index(AHavgDist, AHlowCostArch);
[DfinalDist,DfinalCost,DfinalSci] = get_final_index(DavgDist, DlowCostArch);
[DHfinalDist,DHfinalCost,DHfinalSci] = get_final_index(DHavgDist, DHlowCostArch);

Alabel(logical(ones(length(AfinalDist(:,end)),1))) = {a};
AHlabel(logical(ones(length(AHfinalDist(:,end)),1))) = {ah};
Dlabel(logical(ones(length(DfinalDist(:,end)),1))) = {d};
DHlabel(logical(ones(length(DHfinalDist(:,end)),1))) = {dh};

group = [Alabel; AHlabel; Dlabel; DHlabel];

figure(1)
boxdata = [AfinalDist;AHfinalDist;DfinalDist;DHfinalDist];
boxplot(boxdata,group)

figure(2)
boxdata = [AfinalCost;AHfinalCost;DfinalCost;DHfinalCost];
boxplot(boxdata,group)

figure(3)
boxdata = [AfinalSci;AHfinalSci;DfinalSci;DHfinalSci];
boxplot(boxdata,group)

%get average performance over n iterations
n = 20;
[Aavg_Dist,Aavg_Cost,Aavg_Sci] = avg_n_sp(AavgDist, AlowCostArch,n);
[AHavg_Dist,AHavg_Cost,AHavg_Sci] = avg_n_sp(AHavgDist, AHlowCostArch,n);
[Davg_Dist,Davg_Cost,Davg_Sci] = avg_n_sp(DavgDist, DlowCostArch,n);
[DHavg_Dist,DHavg_Cost,DHavg_Sci] = avg_n_sp(DHavgDist, DHlowCostArch,n);
AvgDist = [Aavg_Dist,AHavg_Dist,Davg_Dist,DHavg_Dist];
AvgCost = [Aavg_Cost,AHavg_Cost,Davg_Cost,DHavg_Cost];
AvgSci = [Aavg_Sci,AHavg_Sci,Davg_Sci,DHavg_Sci];
figure(4)
plot(AvgDist)
legend('Asynchronous: Computer-Only','Asynchronous: Computer-Human','DMAB: Computer-Only','DMAB: Computer-Human')
figure(5)
plot(AvgCost)
legend('Asynchronous: Computer-Only','Asynchronous: Computer-Human','DMAB: Computer-Only','DMAB: Computer-Human')
figure(6)
plot(AvgSci)
legend('Asynchronous: Computer-Only','Asynchronous:a Computer-Human','DMAB: Computer-Only','DMAB: Computer-Human')
end

function [avg_Dist,avg_Cost,avg_Sci] = avg_n_sp(sp_distHist, sp_archHist,n)
[m,~]=size(sp_distHist);
distData = zeros(m,n);
costData = zeros(m,n);
sciData = zeros(m,n);
for i=1:m
    for j=1:n
        distData(i,j) = sp_distHist{i}.get(j-1);
        costData(i,j) = sp_archHist{i}.get(j-1).getResult.getCost;
        sciData(i,j) = sp_archHist{i}.get(j-1).getResult.getScience;
    end
end
avg_Dist = mean(distData,1)';
avg_Cost = mean(costData,1)';
avg_Sci = mean(sciData,1)';
end

function [finalDist,finalCost,finalSci] = get_final_index(dist_history, arch_history)
[m,~]=size(dist_history);
finalDist = zeros(m,1);
finalCost = zeros(m,1);
finalSci = zeros(m,1);
for i=1:m
    last_index = dist_history{i}.size;
    finalDist(i)=dist_history{i}.get(last_index-1);
    finalCost(i)=arch_history{i}.get(last_index-1).getResult.getCost;
    finalSci(i)=arch_history{i}.get(last_index-1).getResult.getScience;
end
end