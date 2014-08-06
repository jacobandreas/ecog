package ecog.features;

import ecog.data.Datum;

/**
 * @author jda
 */
public interface EdgeFeaturizer {
    public String[] apply(Datum datum, int state1, int state2);
}
