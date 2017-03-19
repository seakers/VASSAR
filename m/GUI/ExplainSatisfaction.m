function varargout = ExplainSatisfaction(varargin)
% EXPLAINSATISFACTION MATLAB code for ExplainSatisfaction.fig
%      EXPLAINSATISFACTION, by itself, creates a new EXPLAINSATISFACTION or raises the existing
%      singleton*.
%
%      H = EXPLAINSATISFACTION returns the handle to a new EXPLAINSATISFACTION or the handle to
%      the existing singleton*.
%
%      EXPLAINSATISFACTION('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in EXPLAINSATISFACTION.M with the given input arguments.
%
%      EXPLAINSATISFACTION('Property','Value',...) creates a new EXPLAINSATISFACTION or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before ExplainSatisfaction_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to ExplainSatisfaction_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help ExplainSatisfaction

% Last Modified by GUIDE v2.5 06-May-2014 20:35:48

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @ExplainSatisfaction_OpeningFcn, ...
                   'gui_OutputFcn',  @ExplainSatisfaction_OutputFcn, ...
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


% --- Executes just before ExplainSatisfaction is made visible.
function ExplainSatisfaction_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to ExplainSatisfaction (see VARARGIN)

% Choose default command line output for ExplainSatisfaction
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes ExplainSatisfaction wait for user response (see UIRESUME)
% uiwait(handles.figure1);

set( handles.all_subobj_table, 'Data', create_satisfaction_table );
% set( handles.detail_subobj_table, 'Data', create_satisfaction_table );





% --- Outputs from this function are returned to the command line.
function varargout = ExplainSatisfaction_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;





function subobj_Callback(hObject, eventdata, handles)
% hObject    handle to subobj (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of subobj as text
%        str2double(get(hObject,'String')) returns contents of subobj as a double
global subobj
subobj = get(hObject,'String');
guidata(hObject, handles);

% --- Executes during object creation, after setting all properties.
function subobj_CreateFcn(hObject, eventdata, handles)
% hObject    handle to subobj (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in button_update_subobj.
function button_update_subobj_Callback(hObject, eventdata, handles)
% hObject    handle to button_update_subobj (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global subobj zeResult
ret = capa_vs_req(zeResult,subobj,get_AE,get_params);
set( handles.detail_subobj_table, 'Data', ret );
