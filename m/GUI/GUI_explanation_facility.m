function varargout = GUI_explanation_facility(varargin)
% GUI_explanation_facility MATLAB code for GUI_explanation_facility.fig
%      GUI_explanation_facility, by itself, creates a new GUI_explanation_facility or raises the existing
%      singleton*.
%
%      H = GUI_explanation_facility returns the handle to a new GUI_explanation_facility or the handle to
%      the existing singleton*.
%
%      GUI_explanation_facility('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in GUI_explanation_facility.M with the given input arguments.
%
%      GUI_explanation_facility('Property','Value',...) creates a new GUI_explanation_facility or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before GUI_explanation_facility_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to GUI_explanation_facility_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help GUI_explanation_facility

% Last Modified by GUIDE v2.5 31-May-2014 15:55:26

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @GUI_explanation_facility_OpeningFcn, ...
                   'gui_OutputFcn',  @GUI_explanation_facility_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before GUI_explanation_facility is made visible.
function GUI_explanation_facility_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to GUI_explanation_facility (see VARARGIN)

% Choose default command line output for GUI_explanation_facility
    handles.output = hObject;

    global r;
    global zeResult;
    global results;
    global resMngr;
    global resCol;
    global zeArch;
%     global AE;
%     global params
%     global folder
%     global hm;
    
    javaaddpath('.\java\jess.jar');
    javaaddpath('.\java\jxl.jar');
    javaaddpath('./java/combinatoricslib-2.0.jar');
    javaaddpath('./java/commons-lang3-3.1.jar');
    javaaddpath('./java/matlabcontrol-4.0.0.jar');
    %javaaddpath( '.\java\RBES_Demo_May2014.jar' );
%     javaaddpath( '.\java\RBES_EOSS.jar' );
    javaaddpath( '.\java\CSER2015.jar' );
    import rbsa.eoss.*
    import rbsa.eoss.local.*
    import java.io.*;
    import jess.*;
    
    r = jess.Rete;
    zeResult = [];
    results = [];
    resMngr = [];
    resCol = [];
    zeArch = [];
    get_params;
    get_AE(3);
%     folder = pwd;
%     params = rbsa.eoss.local.Params(folder,'CRISP-ATTRIBUTES','test','normal','');
    set(gcf,'MenuBar','figure');
%     if ~exist('AE','var') || isempty(AE)        
%         
%         
%         AE = rbsa.eoss.ArchitectureEvaluator.getInstance;
%         AE.init(1);
%     end

        
% Update handles structure
    guidata(hObject, handles);

% UIWAIT makes GUI_explanation_facility wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = GUI_explanation_facility_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in buttonUpdatePlot. UPDATE PLOT
function buttonUpdatePlot_Callback(hObject, eventdata, handles)
% hObject    handle to buttonUpdatePlot (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

global archs;
global results;
global architecture;
global resCol;
archs = get_all_archs;
results = resCol.getResults;
archs_str = cell(1, results.size);

for i = 1:length(archs)
%     archs_str{i} = archs{i}.getVariable('id');
     archs_str{i} = ['Architecture ' char(archs{i}.getId)];
end

% Set tehe combobox     to select architectures
set( handles.comboSelectArch, 'String', archs_str );
set( handles.comboSelectArch, 'UserData', [1:(length(archs)) ] );
update_buttons_status( handles, 'off' );
set( handles.num_archs,'String', num2str(length(archs)) );
% results = [];
architecture = [];

tmp = get( handles.comboxVar, 'String' );
x_var = get( handles.comboxVar, 'Value' );
x_var = tmp{x_var};
tmp = get( handles.comboyVar, 'String' );
y_var = get( handles.comboyVar, 'Value' );
y_var = tmp{y_var};
grp_fcn = get( handles.txtGroupFunction, 'String' );
tmp = get( handles.comboParetoFront, 'String' );
pareto = get( handles.comboParetoFront, 'Value' );
pareto = tmp{pareto};

tmp2 = get( handles.fuzzy_crisp_scores, 'String' );
fuzzy = get( handles.fuzzy_crisp_scores, 'Value' );
fuzzy = tmp2{fuzzy};

if strcmp( pareto, 'Yes' )
    pareto = true;
else
    pareto = false;
end
if strcmp( fuzzy, 'Fuzzy' )
    fuzzy = true;
else
    fuzzy = false;
end
RBES_plot25( handles, handles.axes, archs, results, {x_var,y_var}, grp_fcn, pareto, fuzzy);

function RBES_plot25(handles,ax,archs,results, inaxis,filter_func,PARETO, FUZZY)
%     update_utility_table(handles);
    global ref_arch ref_label resCol
    cla reset;% Gives warning
    if FUZZY
        narch = length(archs);
        xvals = zeros(narch,1);
        yvals = zeros(narch,1);
        for i = 1:narch
            xvals(i) = results.get(i-1).getScience;
            yvals(i) = results.get(i-1).getCost;
        end
%         [x_pareto, y_pareto, inds, ~ ] = pareto_front([-xvals yvals] );
        i_pareto = paretofront([-xvals yvals]);
        x_vals2 = xvals(i_pareto);
        y_vals2 = yvals(i_pareto);
        [~, I] = sort(x_vals2);
%         set( handles.num_archs_pf,'String', num2str(length(inds)) );
%         plot( x_vals2(I) , y_vals2(I), 'r--','Parent',ax,'ButtonDownFcn',  {@axes_ButtonDownFcn,archs(inds),x_vals2(I),y_vals2(I),handles});
        narchfront = length(I);
        pareto_archs = archs(i_pareto);
        pareto_archs = pareto_archs (I);
        fuzzyxvals = cell(narchfront,1);
        fuzzyyvals = cell(narchfront,1);
        for i = 1:narchfront
            fuzzyxvals{i} = pareto_archs{i}.getResult.getFuzzy_science();
            fuzzyyvals{i} = pareto_archs{i}.getResult.getFuzzy_cost();
        end
        [~,meanx,meany] = plot_fuzzy_vars(fuzzyxvals,fuzzyyvals);
        xlim( [0 1] );
        ylim( [0 1.2*max(yvals)] );
        hold on;
%         plot( x_vals2(I), y_vals2(I), '--ok','Parent',ax,'ButtonDownFcn',  {@axes_ButtonDownFcn_fuzzy,pareto_archs,x_vals2(I),y_vals2(I),fuzzyxvals(I),fuzzyyvals(I),handles});
        plot( meanx, meany, 'r.','Parent',ax,'ButtonDownFcn',  {@axes_ButtonDownFcn_fuzzy,pareto_archs,meanx,meany,fuzzyxvals,fuzzyyvals,handles});
    else
        MARKERS = {'x','o','d','s','p','.',... 
                'x','o','d','s','p','.',...
                'x','o','d','s','p','.',...
                'x','o','d','s','p','.',...
                'x','o','d','s','p','.'};
            
        COLORS = {'b','m','k','g','c','y',...
                    'm','k','g','c','y','b',...
                    'k','g','c','y','b','m',...
                    'g','c','y','b','m','k',...
                    'c','y','b','m','k','g'};
        narch = length(archs);
        xvals = zeros(narch,1);
        yvals = zeros(narch,1);
        for i = 1:narch
            xvals(i) = results.get(i-1).getScience;
            yvals(i) = results.get(i-1).getCost;
        end
        if isempty(filter_func) || strcmp(filter_func,'') || strcmp(filter_func,' ')
            labels = {'Pareto Front','Architectures'};
            vals = ones(narch,1);
            labels_map = java.util.HashMap;
        else
            eval(['[~,labels_map] = ' filter_func '(archs{1});']);
            if PARETO
                labels = {'Pareto front'};
            else
                labels = {};
            end
            vals = cellfun(str2func(filter_func),archs);
        end
        unique_vals = unique(vals);
        n = length(unique_vals);
        indexes = cell(n,1);
        markers = MARKERS(1:length(unique_vals));
        colors = COLORS(1:length(unique_vals));
        
        %plot reference architectures (search from back b/c ref archs inserted at back)
        ref_colors = {'b','r','g','k','m'};
        [a,b]=size(ref_arch);
        for i = 1:b
            for j = resCol.getResults.size-1:-1:0
                if resCol.getResults.get(j).getArch.getId==ref_arch{i}.getId
                    sci = resCol.getResults.get(j).getScience;
                    cost = resCol.getResults.get(j).getCost;
                    scatter(sci,cost,50,'Marker','h','MarkerEdgeColor',ref_colors{i},'MarkerFaceColor',ref_colors{i},'LineWidth',2,...
                        'Parent',ax);
                    hold on
                    break;
                end
            end
        end
        legend(ref_label,'Location','Best');
        labels = [ref_label labels];
        
        
        if PARETO
%             [x_pareto, y_pareto, inds, ~ ] = pareto_front([-xvals yvals]);
            i_pareto = paretofront([-xvals yvals]);
            x_vals2 = xvals(i_pareto);
            y_vals2 = yvals(i_pareto);
            [~, I] = sort(x_vals2);
            set( handles.num_archs_pf,'String', num2str(length(I)) );
            plot( x_vals2(I) , y_vals2(I), 'k--','Parent',ax,'ButtonDownFcn',  {@axes_ButtonDownFcn,archs(I),x_vals2(I),y_vals2(I),handles});
        end
        hold on;
        xlim( [0 1] );
        ylim( [0 1.2*max(yvals)] );
        for i = 1 : n
            indexes{i} = (vals == unique_vals(i));
            labels = [labels;labels_map.get(unique_vals(i))]; 
            scatter(xvals(indexes{i}),yvals(indexes{i}),'Marker',markers{i},'MarkerEdgeColor',colors{i},...
                    'Parent',ax,'ButtonDownFcn', {@axes_ButtonDownFcn,archs(indexes{i}),xvals(indexes{i}),yvals(indexes{i}),handles});

            hold on;
        end
       

        grid on;
        xlabel(inaxis{1});
        ylabel(inaxis{2});
        ylim( [0 1.2*max(yvals)] );
        legend(labels,'Location','Best');
    end

function update_utility_table(handles)
    global archs utilities
    narchs = length(archs);
    sciences = zeros(narchs,1);
    costs = zeros(narchs,1);
    
    for i = 1:narchs
        sciences(i) = archs{i}.getResult.getNorm_science;
        costs(i) = archs{i}.getResult.getNorm_cost;
    end
    alpha = 0.5;
    utilities = alpha*sciences + (1-alpha)*costs;
    [sorted_u,sorted_ind] = sort(utilities,'descend');
    MAX = 10;
    data = cell(narchs+1,3);
    data(1,:) = {'Rank','Util','Arch id'};
    for i = 1:MAX
        data{i+1,1} = num2str(i);
        data{i+1,2} = num2str(sorted_u(i));
        data{i+1,3} = ['Architecture ' num2str(sorted_ind(i))];
    end
    
    set( handles.utility_table, 'Data', data );
            

    
function axes_ButtonDownFcn(src,eventdata,archs,x,y,handles)

global architecture;
global zeResult;
global zeArch;
global marker_handles;

% Find the closest point arch to the mouse click
mouse = get( handles.axes, 'CurrentPoint' );
xmouse = mouse(1,1);
ymouse = mouse(1,2);
[~,ind] = min( abs((x - xmouse)/(max(x)-min(x))).^2+abs((y - ymouse)/(max(y)-min(y))).^2 );

% Set the architecture for analysis
update_buttons_status( handles, 'off' );
architecture = [];
zeArch = archs{ind};
zeResult = zeArch.getResult;
ind2 = find( strcmp( get( handles.comboSelectArch, 'String' ), ['Architecture ' char(zeArch.getId)] ) );
set( handles.comboSelectArch, 'Value', ind2 );

%create_element_hierarchy;
try
    delete(marker_handles);
end

marker_handles = plot(x(ind),y(ind),'gs', 'MarkerSize', 10 );
update_buttons_status( handles, 'on' );

function axes_ButtonDownFcn_fuzzy(src,eventdata,archs,x,y,fzx,fzy,handles)

global architecture;
global zeResult;
global zeArch;
global marker_handles;

% Find the closest point arch to the mouse click
mouse = get( handles.axes, 'CurrentPoint' );
xmouse = mouse(1,1);
ymouse = mouse(1,2);
[~,ind] = min( abs((x - xmouse)/(max(x)-min(x))).^2+abs((y - ymouse)/(max(y)-min(y))).^2 );

% Set the architecture for analysis
update_buttons_status( handles, 'off' );
architecture = [];
zeArch = archs{ind};
zeResult = zeArch.getResult;
ind2 = find( strcmp( get( handles.comboSelectArch, 'String' ), ['Architecture ' char(zeArch.getId)] ) );
set( handles.comboSelectArch, 'Value', ind2 );

%create_element_hierarchy;
try
    for i = 1:length(marker_handles)
        delete(marker_handles{i});
    end
end
marker_handles = {};
marker_handles{1} = plot(x(ind),y(ind),'ms', 'MarkerSize', 10 );
fx = fzx{ind}.getNum_val;
errxD = fzx{ind}.getInterv.getMin;
errxU = fzx{ind}.getInterv.getMax;
fy= fzy{ind}.getNum_val;
erryD = fzy{ind}.getInterv.getMin;
erryU = fzy{ind}.getInterv.getMax;
h=ploterr(fx,fy,{errxD,errxU},{erryD,erryU},'g.');
set(h(2),'Color','g'), set(h(3),'Color','g'), set(h(1),'MarkerSize',15), set(h(1),'MarkerFaceColor','g');
marker_handles{2} = h;
update_buttons_status( handles, 'on' );



function [x_pareto y_pareto i_pareto i_dominated] =  pareto_front(data, obj)
k = 1;
i_dominated = [];

for arch_i = 1 : size(data,1)
    for arch_j = 1 : size(data,1)
        
        count_i = 0;
        count_j = 0;
        
        for dec = 1:size(obj,2)
            
            if((strcmp(obj(dec), 'SIB')))
                if(data(arch_i, dec) > data(arch_j, dec))
                    count_j = count_j + 1;
                elseif(data(arch_i, dec) < data(arch_j, dec))
                    count_i = count_i + 1;
                end
            elseif (strcmp(obj(dec), 'LIB'))
                if(data(arch_i, dec) < data(arch_j, dec))
                    count_j = count_j + 1;
                elseif(data(arch_i, dec) > data(arch_j, dec))
                    count_i = count_i + 1;
                end
            end
        end
        
        if (count_i == 0 && count_j > 0)
            i_dominated(k) = arch_i;
            k = k+1;
        end
        
    end
    
end

i_dominated = unique(i_dominated);
i_pareto = setdiff(1:1:size(data,1),i_dominated);

% get the pareto frontier points
x_pareto = data(i_pareto,1);
y_pareto = data(i_pareto,2);

% Order the pareto frontier points
[x_pareto,I]=sort(x_pareto');
x_pareto = x_pareto';
y_pareto= y_pareto(I);

% --- Executes on button press in buttonPrintPlot. PRINT PLOT
function buttonPrintPlot_Callback(hObject, eventdata, handles)
% hObject    handle to buttonPrintPlot (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global archs results;

% tmp = get( handles.comboxVar, 'Value' );
% x_var = results{1,tmp};
% tmp = get( handles.comboyVar, 'Value' );
% y_var = results{2,tmp};
tmp = get( handles.comboxVar, 'String' );
x_var = get( handles.comboxVar, 'Value' );
x_var = tmp{x_var};
tmp = get( handles.comboyVar, 'String' );
y_var = get( handles.comboyVar, 'Value' );
y_var = tmp{y_var};

grp_fcn = get( handles.txtGroupFunction, 'String' );
tmp = get( handles.comboParetoFront, 'String' );
pareto = get( handles.comboParetoFront, 'Value' );
pareto = tmp{pareto};
if strcmp( pareto, 'Yes' )
    pareto = true;
else
    pareto = false;
end
xlim = get( handles.axes, 'Xlim' );
ylim = get( handles.axes, 'Ylim' );
print_figure(archs,results, {x_var,y_var},xlim,ylim,grp_fcn,pareto);


function print_figure(archs,results, inaxis,xlim,ylim,filter_func,PARETO)
global ref_arch ref_label resCol
fs = 10;
ms = 80;
f = figure;
ax = axes('FontSize',fs);
% MARKERS = {'x','o','d','s','p','.',...
%     'x','o','d','s','p','.',...
%     'x','o','d','s','p','.',...
%     'x','o','d','s','p','.',...
%     'x','o','d','s','p','.'};
% 
% COLORS = {'b','m','k','g','c','y',...
%     'm','k','g','c','y','b',...
%     'k','g','c','y','b','m',...
%     'g','c','y','b','m','k',...
%     'c','y','b','m','k','g'};
% narch = size(archs,2);
% xvals = zeros(narch,1);
% yvals = zeros(narch,1);
% for i = 1:narch
%     
%     if ~isfield(archs{i},inaxis{1}) || ~isfield(archs{i},inaxis{2})
%         compute_other_metrics(inaxis);
%     end
%     
%     xvals(i) = eval(['archs{i}.' inaxis{1}]);
%     yvals(i) = eval(['archs{i}.' inaxis{2}]);
% end
% if isempty(filter_func) || strcmp(filter_func,'') || strcmp(filter_func,' ')
%     labels = {'Architectures','Pareto Front'};
%     vals = ones(narch,1);
%     filter_func = 'tradespace';
% else
%     eval(['[~,labels] = ' filter_func '(archs{1})']);
%     vals = cellfun(str2func(filter_func),archs);
% end
% unique_vals = unique(vals);
% n = length(unique_vals);
% indexes = cell(n,1);
% markers = MARKERS(1:length(unique_vals));
% colors = COLORS(1:length(unique_vals));
% f = figure;
% ax = axes('FontSize',fs);
% 
% if PARETO
%     [x_pareto, y_pareto, ~, ~ ] = pareto_front([xvals yvals] , {'LIB', 'SIB'});
%     plot( x_pareto, y_pareto, 'r--' );
% end
% 
% for i = 1 : n
%     indexes{i} = (vals == unique_vals(i));
%     scatter(xvals(indexes{i}),yvals(indexes{i}),ms,'Marker',markers{i},'MarkerEdgeColor',colors{i},...
%         'Parent',ax);
%     hold on;
% end
% 
% grid on;
% xlabel(inaxis{1},'FontSize',fs);
% ylabel(inaxis{2},'FontSize',fs);
% set( ax, 'Xlim', xlim );
% set( ax, 'Ylim', ylim );
% legend(labels,'Location','Best','FontSize',fs);
MARKERS = {'x','o','d','s','p','.',... 
            'x','o','d','s','p','.',...
            'x','o','d','s','p','.',...
            'x','o','d','s','p','.',...
            'x','o','d','s','p','.'};

COLORS = {'b','m','k','g','c','y',...
            'm','k','g','c','y','b',...
            'k','g','c','y','b','m',...
            'g','c','y','b','m','k',...
            'c','y','b','m','k','g'};
narch = length(archs);
xvals = zeros(narch,1);
yvals = zeros(narch,1);
for i = 1:narch
    xvals(i) = results.get(i-1).getScience;
    yvals(i) = results.get(i-1).getCost;
end
if isempty(filter_func) || strcmp(filter_func,'') || strcmp(filter_func,' ')
    labels = {'Architectures','Pareto Front'};
    vals = ones(narch,1);
else
    eval(['[~,labels_map] = ' filter_func '(archs{1})']);
    if PARETO
        labels = {'Pareto front'};
    else
        labels = {};
    end
    vals = cellfun(str2func(filter_func),archs);
end
unique_vals = unique(vals);
n = length(unique_vals);
indexes = cell(n,1);
markers = MARKERS(1:length(unique_vals));
colors = COLORS(1:length(unique_vals));

%plot reference architectures (search from back b/c ref archs inserted at back)
ref_colors = {'b','r','g','k','m'};
[a,b]=size(ref_arch);
for i = 1:b
    for j = resCol.getResults.size-1:-1:0
        if resCol.getResults.get(j).getArch.getId==ref_arch{i}.getId
            sci = resCol.getResults.get(j).getScience;
            cost = resCol.getResults.get(j).getCost;
            scatter(sci,cost,50,'Marker','h','MarkerEdgeColor',ref_colors{i},'MarkerFaceColor',ref_colors{i},'LineWidth',2,...
                'Parent',ax);
            hold on
            break;
        end
    end
end
legend(ref_label,'Location','Best');
labels = [ref_label labels];
        
 if PARETO
    [x_pareto, y_pareto, inds, ~ ] = pareto_front([xvals yvals] , {'LIB', 'SIB'});
    plot( x_pareto, y_pareto, 'r--','Parent',ax);
 end
hold on;
for i = 1 : n
    if isempty(filter_func) || strcmp(filter_func,'') || strcmp(filter_func,' ')
    else
        labels = [labels,labels_map.get(unique_vals(i))]; 
    end
    
    indexes{i} = (vals == unique_vals(i));
    scatter(xvals(indexes{i}),yvals(indexes{i}),ms,'Marker',markers{i},'MarkerEdgeColor',colors{i},...
        'Parent',ax);
    hold on;
end

grid on;
xlabel(inaxis{1},'FontSize',fs);
ylabel(inaxis{2},'FontSize',fs);
set( ax, 'Xlim', xlim );
set( ax, 'Ylim', ylim );
legend(labels,'Location','Best','FontSize',fs);


grid on;
xlabel(inaxis{1});
ylabel(inaxis{2});
legend(labels,'Location','Best');
print( f, '-dpng', filter_func );
print( f, '-dmeta', filter_func );
close;



% --- Executes on selection change in comboxVar.
function comboxVar_Callback(hObject, eventdata, handles)
% hObject    handle to comboxVar (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns comboxVar contents as cell array
%        contents{get(hObject,'Value')} returns selected item from comboxVar


% --- Executes during object creation, after setting all properties.
function comboxVar_CreateFcn(hObject, eventdata, handles)
% hObject    handle to comboxVar (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in comboyVar.
function comboyVar_Callback(hObject, eventdata, handles)
% hObject    handle to comboyVar (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns comboyVar contents as cell array
%        contents{get(hObject,'Value')} returns selected item from comboyVar


% --- Executes during object creation, after setting all properties.
function comboyVar_CreateFcn(hObject, eventdata, handles)
% hObject    handle to comboyVar (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in comboParetoFront.
function comboParetoFront_Callback(hObject, eventdata, handles)
% hObject    handle to comboParetoFront (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns comboParetoFront contents as cell array
%        contents{get(hObject,'Value')} returns selected item from comboParetoFront


% --- Executes during object creation, after setting all properties.
function comboParetoFront_CreateFcn(hObject, eventdata, handles)
% hObject    handle to comboParetoFront (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function txtGroupFunction_Callback(hObject, eventdata, handles)
% hObject    handle to txtGroupFunction (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of txtGroupFunction as text
%        str2double(get(hObject,'String')) returns contents of txtGroupFunction as a double


% --- Executes during object creation, after setting all properties.
function txtGroupFunction_CreateFcn(hObject, eventdata, handles)
% hObject    handle to txtGroupFunction (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in buttonArchitecture.
function buttonArchitecture_Callback(hObject, eventdata, handles)
% hObject    handle to buttonArchitecture (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
ArchitectureWnd;

% --- Executes on button press in buttonSatisfaction.
function buttonSatisfaction_Callback(hObject, eventdata, handles)
% hObject    handle to buttonSatisfaction (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
ExplainSatisfaction

% --- Executes on button press in buttonCost.
function buttonCost_Callback(hObject, eventdata, handles)
% hObject    handle to buttonCost (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
ExplainCost

% --- Executes on button press in buttonSCDesign.
function buttonSCDesign_Callback(hObject, eventdata, handles)
% hObject    handle to buttonSCDesign (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
ExplainSCDesign

% % --- Executes on selection change in comboSelectArch.
% function popupmenu1_Callback(hObject, eventdata, handles)
% % hObject    handle to comboSelectArch (see GCBO)
% % eventdata  reserved - to be defined in a future version of MATLAB
% % handles    structure with handles and user data (see GUIDATA)
% 
% % Hints: contents = cellstr(get(hObject,'String')) returns comboSelectArch contents as cell array
% %        contents{get(hObject,'Value')} returns selected item from comboSelectArch

function comboSelectArch_Callback(hObject, eventdata, handles)
% hObject    handle to comboSelectArch (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns comboSelectArch contents as cell array
%        contents{get(hObject,'Value')} returns selected item from comboSelectArch

global zeResult;
global archs;
global zeArch;
global marker_handles;

selArch = get( handles.comboSelectArch, 'Value' );
zeResult = archs{selArch}.getResult;
zeArch = archs{selArch};

%create_element_hierarchy;
update_buttons_status( handles, 'on' );
try
    delete(marker_handles);
end
x = get(get(handles.axes, 'Children'), 'XData');
y = get(get(handles.axes, 'Children'), 'YData');
if(size(x,1)>1)
    x = x{end};
    y = y{end};
end
marker_handles = plot(x(selArch),y(selArch),'yo', 'MarkerSize', 10 );


function update_buttons_status( handles, status )

set( handles.buttonArchitecture, 'Enable', status );
% set( handles.buttonFacts, 'Enable', status );
set( handles.buttonSatisfaction, 'Enable', status );
set( handles.buttonCost, 'Enable', status );
% set( handles.buttonSchedule, 'Enable', status );
set( handles.buttonSCDesign, 'Enable', status );
%set( handles.buttonWindows, 'Enable', status );
set( handles.buttonEvalArch, 'Enable', status );
set( handles.buttonEvaluateNewArchitecture, 'Enable', status );



% --- Executes during object creation, after setting all properties.
function comboSelectArch_CreateFcn(hObject, eventdata, handles)
% hObject    handle to comboSelectArch (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in buttonEvalArch.
function buttonEvalArch_Callback(hObject, eventdata, handles)
% hObject    handle to buttonEvalArch (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

global zeArch;
global resCol;
global zeResult;
global archs;
% global AE
% zeArch = archs{get(handles.comboSelectArch, 'Value')};
AE = get_AE();
zeArch.setEval_mode('DEBUG');
disp('Evaluating arch...');
res = AE.evaluateArchitecture(zeArch,'Slow');
for i = 0:resCol.getResults.size-1
    if strcmp( resCol.getResults.get(i).getArch.getId, zeArch.getId )
        resCol.getResults.push(res);
    end
end
disp('Done.');
zeResult = res;
% --- Executes on button press in buttonEvaluateNewArchitecture.
function buttonEvaluateNewArchitecture_Callback(hObject, eventdata, handles)
% hObject    handle to buttonEvaluateNewArchitecture (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
NewArchitectureWnd;

% --- Executes on button press in pushbutton1. LOAD FILE
function pushbutton1_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global results
global architecture;
global archs;
global resMngr;
global resCol;
global folder;
global r;
global qb;
global hm;
% global AE;
global ref_arch ref_label

%clearvars -global results architecture resCol x y

[FileName,PathName,FilterIndex] = uigetfile( './*.*' );
% params = rbsa.eoss.local.Params(folder,'CRISP-ATTRIBUTES','test','normal','');
resMngr = rbsa.eoss.ResultManager.getInstance();
resCol = resMngr.loadResultCollectionFromFile( [PathName FileName] );
results = resCol.getResults;
[ref_arch,ref_label]=setReferenceArchs();


set( handles.txtFilePath, 'String', FileName );
conf = resCol.getConf;
reqs = char(conf.get('Requirements'));
tmp = find(reqs=='\',100,'last');
set( handles.txtInputFile, 'String',  reqs(tmp(end)+1:end));

capas = char(conf.get('Capabilities'));
tmp = find(capas=='\',100,'last');
set( handles.txtCapaFile, 'String',  capas(tmp(end)+1:end));

set( handles.txtTimeStamp, 'String', char(resCol.getStamp) );

archs = get_all_archs;
% metrics_labels = {'Benefit','Lifecyle cost'};
% metrics = {'benefit','lifecycle-cost'};
% hm = java.util.HashMap;
% for i = 1:length(metrics)
%     hm.put(metrics_labels{i},metrics{i});
% end
% % Check is a metric has been computed
% if ~isempty(archs{i}.getOtherData)
%     for i = 1:length(metrics)
%         if archs{i}.getOtherData.keySet.contains(metrics{i}) == 1
%             metrics(i) = [];
%         end
%     end
% end
archs_str = cell(1, resCol.getResults.size);
for i = 1:length(archs)
%     archs_str{i} = archs{i}.getVariable('id');
%     archs_str{i} = ['Architecture ' num2str(i)]
    archs_str{i} = ['Architecture ' char(archs{i}.getId)];
end

% Set tehe combobox     to select architectures
set( handles.comboSelectArch, 'String', archs_str );
set( handles.comboSelectArch, 'UserData', [1:(length(archs)) ] );
update_buttons_status( handles, 'off' );
% results = [];
architecture = [];

% Set the combobox for the x-axis
% set( handles.comboxVar, 'String', [{'benefit'} metrics_labels] );
set( handles.comboxVar, 'String', [{'benefit','lifecycle-cost','fuzzy-cost'}] );

% Set the combobox for the y-axis
% set( handles.comboyVar, 'String', [{'lifecycle-cost'} metrics_labels] );
set( handles.comboyVar, 'String', [{'lifecycle-cost','benefit','fuzzy-cost'}] );
% Set the combobox for pareto front
set( handles.comboParetoFront, 'String', {'Yes','No'} );
set( handles.fuzzy_crisp_scores, 'String', {'Crisp','Fuzzy'} );
% Compute extra information needed
%compute_other_metrics(metrics);


function txtFilePath_Callback(hObject, eventdata, handles)
% hObject    handle to txtFilePath (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of txtFilePath as text
%        str2double(get(hObject,'String')) returns contents of txtFilePath as a double


% --- Executes during object creation, after setting all properties.
function txtFilePath_CreateFcn(hObject, eventdata, handles)
% hObject    handle to txtFilePath (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function txtInputFile_Callback(hObject, eventdata, handles)
% hObject    handle to txtInputFile (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of txtInputFile as text
%        str2double(get(hObject,'String')) returns contents of txtInputFile as a double


% --- Executes during object creation, after setting all properties.
function txtInputFile_CreateFcn(hObject, eventdata, handles)
% hObject    handle to txtInputFile (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function txtTimeStamp_Callback(hObject, eventdata, handles)
% hObject    handle to txtTimeStamp (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of txtTimeStamp as text
%        str2double(get(hObject,'String')) returns contents of txtTimeStamp as a double


% --- Executes during object creation, after setting all properties.
function txtTimeStamp_CreateFcn(hObject, eventdata, handles)
% hObject    handle to txtTimeStamp (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in buttonSaveRC.
function buttonSaveRC_Callback(hObject, eventdata, handles)
% hObject    handle to buttonSaveRC (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)



function txtCapaFile_Callback(hObject, eventdata, handles)
% hObject    handle to txtCapaFile (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of txtCapaFile as text
%        str2double(get(hObject,'String')) returns contents of txtCapaFile as a double


% --- Executes during object creation, after setting all properties.
function txtCapaFile_CreateFcn(hObject, eventdata, handles)
% hObject    handle to txtCapaFile (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in fuzzy_crisp_scores.
function fuzzy_crisp_scores_Callback(hObject, eventdata, handles)
% hObject    handle to fuzzy_crisp_scores (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns fuzzy_crisp_scores contents as cell array
%        contents{get(hObject,'Value')} returns selected item from fuzzy_crisp_scores


% --- Executes during object creation, after setting all properties.
function fuzzy_crisp_scores_CreateFcn(hObject, eventdata, handles)
% hObject    handle to fuzzy_crisp_scores (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
