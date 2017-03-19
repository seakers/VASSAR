%% test_architectures_Iridium.m
% CONSTANTS
BIOMASS = 1;
LORENTZ_ERB = 2;
CTECS = 3;
GRAVITY = 4;
SPECTROM = 5;
MICROMAS = 6;

CTECS_AND_GRAV = 7;
ALL_BUT_MICROMAS = 8;
ALL_SYSTEM = 9;
%% Number of sats
Thirty_three_sats = repmat([1 0],[1 33]);
One_sat = [1 zeros(1,65)];
One_per_plane = repmat([1 zeros(1,10)],[1 6]);
All_sats = ones(1,66);
Sat01_in_each_plane = repmat([1 0 0 0 0 0 0 0 0 0 0],[1 6]);
Sat02_in_each_plane = repmat([0 1 0 0 0 0 0 0 0 0 0],[1 6]);
Sat03_in_each_plane = repmat([0 0 1 0 0 0 0 0 0 0 0],[1 6]);
Sat04_in_each_plane = repmat([0 0 0 1 0 0 0 0 0 0 0],[1 6]);
Sat05_in_each_plane = repmat([0 0 0 0 1 0 0 0 0 0 0],[1 6]);
Sat06_in_each_plane = repmat([0 0 0 0 0 1 0 0 0 0 0],[1 6]);
Sat07_in_each_plane = repmat([0 0 0 0 0 0 1 0 0 0 0],[1 6]);
Sat08_in_each_plane = repmat([0 0 0 0 0 0 0 1 0 0 0],[1 6]);
Sat09_in_each_plane = repmat([0 0 0 0 0 0 0 0 1 0 0],[1 6]);
Sat10_in_each_plane = repmat([0 0 0 0 0 0 0 0 0 1 0],[1 6]);
Sat11_in_each_plane = repmat([0 0 0 0 0 0 0 0 0 0 1],[1 6]);

%% Single sensor
% Arch 1: 33 satellites with BIOMASS
arch1 = BIOMASS.*Thirty_three_sats;

% Arch 2: 33 satellites with LORENTZ_ERB
arch2 = LORENTZ_ERB.*Thirty_three_sats;

% Arch 3: 33 satellites with CTECTS
arch3 = CTECS.*Thirty_three_sats;

% Arch 4: 33 satellites with GRAVITY
arch4 = GRAVITY.*Thirty_three_sats;

% Arch 5: 33 satellites with SPECTROM
arch5 = SPECTROM.*Thirty_three_sats;

% Arch 6: 33 satellites with MICROMAS
arch6 = MICROMAS.*Thirty_three_sats;

%% Two sensors

% Arch 7: 33 satellites with CTECS AND GRAVITY sensors
arch7 = CTECS_AND_GRAV.*Thirty_three_sats;

%% All sensors

% Arch 8: = 18 GPSRO, 18 GRAVITY, 12 SPECTROM, 12 BIOMASS, 6 LORENTZ_ERB
arch8 = CTECS*(Sat01_in_each_plane + Sat05_in_each_plane + Sat09_in_each_plane); % 6 planes x 3 sats per plane = 18 GPSRO
arch8 = arch8 + GRAVITY*(Sat02_in_each_plane + Sat06_in_each_plane + Sat10_in_each_plane);% 6 planes x 3 sats per plane = 18 GRAVITY
arch8 = arch8 + SPECTROM*(Sat03_in_each_plane + Sat07_in_each_plane);% 6 planes x 2 sats per plane = 12 SPECTROM
arch8 = arch8 + BIOMASS*(Sat04_in_each_plane + Sat08_in_each_plane);% 6 planes x 2 sats per plane = 12 BIOMASS
arch8 = arch8 + LORENTZ_ERB*(Sat11_in_each_plane);% 6 planes x 1 sats per plane = 6 LORENTZ_ERB

% Arch 9: all but Micromas on all 66 satellites
arch9 = ALL_SYSTEM*All_sats; 
