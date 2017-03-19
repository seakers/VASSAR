function check_scores(lambda)
%% This function prints the scores in each orbit of each subset of instruments that verifies the lambda function
% Ex1: check_scores(@(x)x.size==1)
% Ex2: check_scores(@(x)x.contains('DESD_SAR') && x.size == 1)
    AE = get_AE;
    scores = AE.getScores;
    keys = scores.keySet.iterator;
    while keys.hasNext
        key = keys.next;
        score = scores.get(key);
        if lambda(key)
            fprintf('%s : %s\n',char(key.toString),char(score.toString));
        end
    end
end