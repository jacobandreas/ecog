package ecog.features;

import ecog.data.Dataset;
import ecog.data.Datum;

/**
 * @author jda
 */
public class SimpleNodeFeaturizer implements NodeFeaturizer {
    @Override
    public String[] apply(Datum datum, int state, int frame) {
        String[] feats = new String[Dataset.N_ELECTRODES];
        double[] totalActivations = new double[Dataset.N_ELECTRODES];
        for (int t = frame + 15; t < frame + 20; t++) {
            if (t >= datum.response.length) break;
            for (int e = 0; e < Dataset.N_ELECTRODES; e++) {
//                System.out.println(t);
//                System.out.println(e);
//                System.out.println(totalActivations.length);
//                System.out.println(datum.response.length);
//                System.out.println(datum.response[t].length);
//                System.out.println();
                totalActivations[e] += datum.response[t][e];
            }
        }
        for (int e = 0; e < Dataset.N_ELECTRODES; e++) {
            feats[e] = "STATE_" + state + "_E_" + e + "_" + (int)(totalActivations[e] / 5 * 2);
        }
        return feats;
    }
}
