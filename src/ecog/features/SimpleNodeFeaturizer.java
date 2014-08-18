package ecog.features;

import ecog.data.Dataset;
import ecog.data.Datum;

import java.util.Arrays;

/**
 * @author jda
 */
public class SimpleNodeFeaturizer implements NodeFeaturizer {
    @Override
    public String[] apply(Datum datum, int state, int t, int frame) {
        String[] feats = new String[Dataset.N_ELECTRODES * 3 + 1];
        double[] preActivations = new double[Dataset.N_ELECTRODES];
        double[] midActivations = new double[Dataset.N_ELECTRODES];
        double[] postActivations = new double[Dataset.N_ELECTRODES];
        for (int f = frame - 5; f < frame; f++) {
            if (f < 0) continue;
            for (int e = 0; e < Dataset.N_ELECTRODES; e++) {
                preActivations[e] += datum.response[f][e];
            }
        }
        for (int f = frame + 15; f < frame + 20; f++) {
            if (f >= datum.response.length) continue;
            for (int e = 0; e < Dataset.N_ELECTRODES; e++) {
                midActivations[e] += datum.response[f][e];
            }
        }
        for (int f = frame + 20; f < frame + 25; f++) {
            if (f >= datum.response.length) continue;
            for (int e = 0; e < Dataset.N_ELECTRODES; e++) {
                postActivations[e] += datum.response[f][e];
            }
        }
        for (int e = 0; e < Dataset.N_ELECTRODES; e++) {
            feats[e] = "STATE_PRE_" + state + "_E_" + e + "_" + (int)(preActivations[e] / 5 * 2);
            feats[Dataset.N_ELECTRODES + e] = "STATE_MID_" + state + "_E_" + e + "_" + (int)(midActivations[e] / 5 * 2);
            feats[2 * Dataset.N_ELECTRODES + e] = "STATE_POST_" + state + "_E_" + e + "_" + (int)(postActivations[e] / 5 * 2);
        }
        feats[feats.length-1] = "STATE_" + state + "_CONST";
        //feats[feats.length-1] = "STATE_" + state + "_" + datum.tokenBoundaries[t];
        //System.out.println(Arrays.toString(feats));
        return feats;
    }
}
