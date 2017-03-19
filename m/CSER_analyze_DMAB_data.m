function CSER_analyze_DMAB_data

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

%load agent stat files
disp('Choose agent stat files');
[filename, pathname, filterindex] = uigetfile('*.rs', 'Pick a results file','MultiSelect','on');

ASH = AgentSelectionHistory.getInstance;

if(~iscell(filename))
    agentData = ASH.loadSelectionHistoryFromFile(strcat(pathname,filename)).getSelectionHistory;
else
    agentData = cell(length(filename),1);
    for i=1:length(filename)
        agentData{i} = ASH.loadSelectionHistoryFromFile(strcat(pathname,filename{i})).getSelectionHistory;
    end
end

modes = ASH.getModes;
numAgents = modes.length;
max_data_length = 0;
for i =1:length(filename)
    if agentData{i}.size > max_data_length
        max_data_length = agentData{i}.size;
    end
end
avg_history = zeros(max_data_length,numAgents);
resetCount = 0;
for i=1:length(filename)
    history = agentData{i};
    for j = 1:history.size
        for k=1:numAgents
            if history.get(j-1).equals(modes(k))
                avg_history(j,k) = avg_history(j,k)+ 1;
            end
        end
    end
%     resetCount = resetCount + history.getResetNum();
end

avg_history = avg_history/length(filename);
% avg_resetCount = resetCount/length(filename);
% fprintf('average reset count was %d',avg_resetCount);

%all on one plot
figure(1);
hold on;
pattern = {'-b','-g','-r','-c','-m','-k',...
           ':b',':g',':r',':c',':m',':k'};
for i=1:numAgents
    plot(avg_history(:,i),pattern{i});
    disp(char(modes(i)))
    disp(mean(avg_history(:,i)))
    disp(std(avg_history(:,i)))
    labels{i}=char(modes(i));
end
legend(labels);

%plot on subplot
figure(2);
for i=1:numAgents
    subplot(numAgents,1,i)
     plot(avg_history(:,i))
    legend(char(modes(i)));
    axis([0,5000,0,1]);
end


end