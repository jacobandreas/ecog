package ecog.features;

import counter.Counter;
import ecog.data.Datum;

/**
 * @author jda
 */
public interface EdgeFeaturizer {
    public Counter<String> apply(Datum datum, int state1, int state2);
}
