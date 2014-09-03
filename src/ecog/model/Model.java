package ecog.model;

import ecog.data.Datum;
import ecog.data.LabeledDatum;
import ecog.eval.EvalStats;

import java.util.List;
import java.util.Set;

import tuple.Pair;

/**
 * @author jda
 */
public abstract class Model {
    public abstract LabeledDatum predict(Datum datum);
    public EvalStats evaluate(List<LabeledDatum> data) {
        int total = 0, correct = 0;
        for (LabeledDatum datum : data) {
            LabeledDatum predDatum = predict(datum);
            for (int i = 0; i < datum.labels.length; i++) {
                if (datum.labels[i].label.equals(predDatum.labels[i].label)) {
                    correct += 1;
                }
                total += 1;
            }
        }
        return new EvalStats(total, correct);
    }
    public Pair<EvalStats,EvalStats> evaluateLabelSubset(List<LabeledDatum> data, Set<String> labelSubset) {
    	int totalRecall = 0, correctRecall = 0, totalPrec = 0, correctPrec = 0;
    	for (LabeledDatum datum : data) {
    		LabeledDatum predDatum = predict(datum);
    		for (int i = 0; i < datum.labels.length; i++) {
    			if (labelSubset.contains(datum.labels[i].label)) {
    				if (datum.labels[i].label.equals(predDatum.labels[i].label)) {
    					correctRecall += 1;
    				}
    				totalRecall += 1;
    			}
    			if (labelSubset.contains(predDatum.labels[i].label)) {
    				if (datum.labels[i].label.equals(predDatum.labels[i].label)) {
    					correctPrec += 1;
    				}
    				totalPrec += 1;
    			}
    		}
    	}
    	return Pair.makePair(new EvalStats(totalPrec, correctPrec), new EvalStats(totalRecall, correctRecall));
    }
}
