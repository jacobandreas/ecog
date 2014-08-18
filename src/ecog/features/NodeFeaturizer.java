package ecog.features;

import ecog.data.Datum;

/**
 * @author jda
 */
public interface NodeFeaturizer {
    public String[] apply(Datum datum, int state, int t, int frame);
}
