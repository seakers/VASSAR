function test_sorted_plot(src,eventdata,archs,results,metric)
global params
mouse = get(gca, 'CurrentPoint');
xmouse = mouse(1,1);
ymouse = mouse(1,2);
values = results.(metric);
rankings = 1:length(values);
[val, i] = min(abs((rankings - xmouse)/xmouse).^2+abs((values - ymouse)/ymouse).^2);
xpoint   = rankings(i);
ypoint   = values(i);
arch = archs(i,:);

fprintf('Arch = %d, ranking = %d, value = %f\n',i,rankings(i),values(i));
if strcmp(params.MODE,'SELECTION')
    str = SEL_arch_to_str(archs(i,:));
    fprintf('Sequence = %s\n',str);
elseif strcmp(params.MODE,'PACKAGING')
    str = PACK_arch_to_str(archs(i,:));
    fprintf('Sequence = %s\n',str);
elseif strcmp(params.MODE,'SCHEDULING') % archs,discounted_values,data_continuities,utilities,pareto_ranks,programmatic_risks,fairness,params
    str = SCHED_arch_to_str(archs(i,:));
    fprintf('Sequence = %s\n',str);
end
end