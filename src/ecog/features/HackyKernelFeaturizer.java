package ecog.features;

import arrays.a;
import counter.Counter;
import ecog.data.Dataset;
import ecog.data.Datum;
import ecog.data.LabeledDatum;
import indexer.Indexer;
import tuple.Pair;

import java.util.*;

/**
 * @author jda
 */
public class HackyKernelFeaturizer implements NodeFeaturizer {

    private final double[][] basis;
    private final double radius;
    private final Map<Pair<Datum,Integer>,double[]> weightCache;
    private final int[] exemplarStates;

    public HackyKernelFeaturizer(List<LabeledDatum> data, double radius, Indexer<String> labelIndex) {
        //this.basis = new ArrayList<Datum>(basis);
        this.radius = radius;
        this.weightCache = new HashMap<Pair<Datum,Integer>,double[]>();

        ArrayList<double[]> exemplars = new ArrayList<double[]>();
        for (Datum d : data) {
            for (int t = 0; t < d.tokenBoundaries.length; t++) {
                exemplars.add(d.computeAverageForTimestep(t));
            }
        }
        basis = exemplars.toArray(new double[exemplars.size()][Dataset.N_ELECTRODES]);

        this.exemplarStates = new int[exemplars.size()];
        int i = 0;
        for (LabeledDatum d : data) {
            for (int t = 0; t < d.tokenBoundaries.length; t++) {
//                System.out.println(d.phoneLabels[t].label);
//                System.out.println(labelIndex.getIndex(d.phoneLabels[t].label));
                exemplarStates[i++] = labelIndex.getIndex(d.phoneLabels[t].label);
            }
        }

//        System.out.println(Arrays.toString(exemplarStates));
    }

    @Override
    public Counter<String> apply(Datum datum, int state, int t, int frame) {
        Counter<String> r = new Counter<String>();
        double[] weights = computeWeights(datum, t);
        for (int i = 0; i < weights.length; i++) {
            if (exemplarStates[i] != state) {
                continue;
            }
            r.setCount("K_" + i + "_" + state, weights[i]);
        }
        return r;
    }

    private double[] computeWeights(Datum d1, int t) {
        Pair<Datum,Integer> key = new Pair<Datum,Integer>(d1, t);
        double[] weights;
        if (weightCache.containsKey(key)) {
            weights = weightCache.get(key);
        } else {
            double[] dists = sqDistances(d1, t);
            //System.out.println(Arrays.toString(dists));
            //weight = Math.exp(- dists / radius);
            weights = a.exp(a.scale(dists, -1 / radius));
            weightCache.put(key, weights);
            //System.out.println(Arrays.toString(weights));
        }
        return weights;
    }

    private double[] sqDistances(Datum datum, int t) {
        double[] r = new double[basis.length];
        for (int i = 0; i < basis.length; i++) {
            double[] datumMean = datum.computeAverageForTimestep(t);
            double[] basisMean = basis[i];
            double[] diff = a.comb(datumMean, 1, basisMean, -1);
            double sqDist = a.innerProd(diff, diff);
            r[i] = sqDist;
        }
        return r;
    }

    private double sqDistance(Datum d1, Datum d2) {
        double r = 0;
        for (int i = 0; i < d1.response.length; i++) {
            for (int j = 0; j < d1.response[i].length; j++) {
                double diff = d1.response[i][j] - d2.response[i][j];
                r += diff * diff;
            }
        }
        return r;
    }

}
