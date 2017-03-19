function CSER_analyze_Ateam_data

path = 'C:\Users\Nozomi\Documents\CSER_2015';
cd(path);

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

%load agent stat files
disp('Choose agent stat files');
[filename, pathname, filterindex] = uigetfile('*.rs', 'Pick a results file','MultiSelect','on');


if(~iscell(filename))
    agentData = collectAgentData(strcat(pathname,filename));
else
    agentData = cell(length(filename),1);
    for i=1:length(filename)
        [agentData{i},agentNames] = collectAgentData(strcat(pathname,filename{i}));
    end
end

numAgents = length(agentNames);
averagePerf=cell(numAgents,1);
%1st col gets average credits, 2nd col is average # evals
for i=1:length(agentData)
    oneIteration = agentData{i,1};
    for j=1:numAgents
        if isempty(averagePerf{j,1})
            averagePerf(j,1)=oneIteration(j,1);
            averagePerf(j,2)={length(oneIteration{j,1})};
        else
            averagePerf(j,1) = {sumUpAgentPerformance(averagePerf{j,1},oneIteration{j,1})};
            averagePerf(j,2)={averagePerf{j,2}+length(oneIteration{j,1})};
        end
    end
end

for j=1:numAgents
    averagePerf(j,1) = {averagePerf{j,1}/length(agentData)};
    averagePerf(j,2) = {averagePerf{j,2}/length(agentData)};
end

%all on one plot
figure(1);
hold on;
pattern = {'-b','-g','-r','-c','-m','-k',...
           ':b',':g',':r',':c',':m',':k'};
for i=1:numAgents
    plot(averagePerf{i,1},pattern{i});
    labels{i}=strcat(char(agentNames{i}),' (',num2str(averagePerf{i,2}),')');
end
legend(labels);

%plot on subplot
figure(2);
for i=1:numAgents
    subplot(numAgents,1,i)
    plot(averagePerf{i,1});
    legend(strcat(char(agentNames{i}),' (',num2str(averagePerf{i,2}),')'));
    axis([1,length(averagePerf{i,1}),-1,1]);
end


end

function sum = sumUpAgentPerformance(run1, run2)
if(length(run1)>length(run2))
    run2(length(run1)) = 0;
elseif (length(run1)<length(run2))
    run1(length(run2)) = 0;
end
sum = run1+run2;
end