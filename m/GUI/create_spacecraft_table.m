function data = create_spacecraft_table( hTable)

    global zeResult;

    % Mass budgets
    n = 1;
    facts = zeResult.getCost_facts();
    [tmp] = get_facts_values( facts, {'Name', 'payload-mass#', 'EPS-mass#','ADCS-mass#',...
                                    'thermal-mass#','avionics-mass#','propulsion-mass#','structure-mass#',...
                                    'propellant-mass-injection','propellant-mass-ADCS',...
                                    'satellite-dry-mass','satellite-wet-mass','adapter-mass', ...
                                    'satellite-launch-mass'} );
                                
	data(n:n+12,1) = {'Payload','EPS','ADCS','Thermal','Comm+Avionics', ...
                      'Propulsion (AKM)','Structure','Propellant injection', ...
                      'Propellant ADCS+deorbit','dry mass','wet mass','adapter','launch mass'}';
                                
    for i = 1:size( tmp, 2 )
       data(n:n+12,1+i) = tmp(2:end,i);
    end

    set( hTable, 'ColumnName', [ {''} tmp( 1, : ) ] );
    set( hTable, 'Data', data );
    

    

end