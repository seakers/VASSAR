function ca = ArrayList2CellArray(al)
    n = al.size;
    ca = cell(1,n);
    for i = 1:n
        ca{i} = al.get(i-1);
    end
end