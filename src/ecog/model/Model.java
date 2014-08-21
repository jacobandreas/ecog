package ecog.model;

import ecog.data.Datum;
import ecog.data.LabeledDatum;
import ecog.eval.EvalStats;

import java.util.List;

/**
 * @author jda
 */
public abstract class Model {
    public abstract LabeledDatum predict(Datum datum);
    public EvalStats evaluate(List<LabeledDatum> data) {
        //int tp = 0, fp = 0, fn = 0;
        int total = 0, correct = 0;
        for (LabeledDatum datum : data) {
            LabeledDatum predDatum = predict(datum);
            for (int i = 0; i < datum.labels.length; i++) {
                if (datum.labels[i].label.equals(predDatum.labels[i].label)) {
                    //tp += 1;
                    correct += 1;
                } else {
                    //fn += 1;
                }
                total += 1;
            }
        }
        return new EvalStats(total, correct);
    }
}
