package ecog.model;

import ecog.data.Datum;
import ecog.data.LabeledDatum;
import ecog.eval.EvalStats;

import java.util.List;

/**
 * @author jda
 */
public interface Model {
    public LabeledDatum predict(Datum datum);
    public EvalStats evaluate(List<LabeledDatum> data);
}
