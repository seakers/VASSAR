function [avgParetoDistance,lowestCostMaxSciArch] = loadSearchPerf(prompt)

import rbsa.eoss.*
import rbsa.eoss.local.*
import madkitdemo3.*
import agentIterfaces.*
import java.io.*;

[filename, pathname, filterindex] = uigetfile('*.rs', prompt,'MultiSelect','on');

avgParetoDistance = cell(length(filename),1);
lowestCostMaxSciArch =  cell(length(filename),1);
spm = SearchPerformanceManager.getInstance;

for i=1:length(filename)
    spc = spm.loadSearchPerformanceComparatorFromFile(strcat(pathname,filename{i}));
    
    avgParetoDistance(i) = spc.getAvg_pareto_distances;
    lowestCostMaxSciArch(i) = spc.getLowest_cost_max_science_arch;
end