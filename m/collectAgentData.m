function [data,agent_names] = collectAgentData(filename)

import rbsa.eoss.*
import rbsa.eoss.local.*
import madkitdemo3.*
import java.io.*;

AEC = AgentEvaluationCounter.getInstance();
data = AEC.loadAgentStatFromFile(filename);
stats= data.getAgentDominanceHistory;

keys = stats.keySet();
iter = keys.iterator();

numAgents = keys.size();
data = cell(numAgents,1);
agent_names = cell(numAgents,1);
i=1;
while(iter.hasNext())
    name = iter.next();
    agent_names{i}=char(name);
    javaArray = stats.get(name);
    arrayIter = javaArray.iterator;
    array = zeros(javaArray.size(),1);
    j=1;
    while(arrayIter.hasNext())
        array(j) = arrayIter.next();
        j=j+1;
    end
    data{i} = array;
    i=i+1;
end