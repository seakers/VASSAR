function ret = MatlabCellArray2javaArrayList(ca)
    ret = java.util.ArrayList;
    for i = 1:length(ca)
        ret.add(ca{i});
    end
end