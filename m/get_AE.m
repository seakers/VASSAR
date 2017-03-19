function AE = get_AE(n)
    persistent archEval
    if isempty(archEval)
        archEval = rbsa.eoss.ArchitectureEvaluator.getInstance;
        archEval.init(n);
    end
    AE = archEval;
end
