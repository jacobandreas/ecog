package ecog.features;

import counter.Counter;
import ecog.data.Datum;

/**
 * @author jda
 */
public interface NodeFeaturizer {
    public Counter<String> apply(Datum datum, int state, int t, int frame);
}
