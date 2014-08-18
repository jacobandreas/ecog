package ecog.features;

import ecog.data.Datum;

/**
 * @author jda
 */
public class SimpleEdgeFeaturizer implements EdgeFeaturizer {
    @Override
    public String[] apply(Datum datum, int state1, int state2) {
        //return new String[] { "EDGE__" + state1 + "_" + state2 };
        return new String[0];
    }
}
