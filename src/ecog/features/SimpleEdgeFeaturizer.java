package ecog.features;

import counter.Counter;
import ecog.data.Datum;

/**
 * @author jda
 */
public class SimpleEdgeFeaturizer implements EdgeFeaturizer {
    @Override
    public Counter<String> apply(Datum datum, int state1, int state2) {
        //return new String[] { "EDGE__" + state1 + "_" + state2 };
        Counter<String> r = new Counter<String>();
        r.setCount("EDGE_" + state1 + "_" + state2, 1);
        return r;
    }
}
