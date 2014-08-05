package ecog.model;

import ecog.data.Datum;
import ecog.data.LabeledDatum;
import ecog.eval.EvalStats;

import java.util.List;

/**
 * @author jda
 */
public class CRFModel implements Model {

    public static CRFModel train(List<LabeledDatum> data) {
        return null;
    }

    @Override
    public LabeledDatum predict(Datum datum) {
        return null;
    }

    @Override
    public EvalStats evaluate(List<LabeledDatum> data) {
        return null;
    }
}

