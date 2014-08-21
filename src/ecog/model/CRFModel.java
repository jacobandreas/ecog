package ecog.model;

import counter.Counter;
import ecog.data.Datum;
import ecog.data.LabeledDatum;
import ecog.data.Token;
import ecog.eval.EvalStats;
import ecog.features.EdgeFeaturizer;
import ecog.features.NodeFeaturizer;
import ecog.main.EcogExperiment;
import indexer.HashMapIndexer;
import indexer.Indexer;
import opt.DifferentiableFunction;
import opt.EmpiricalGradientTester;
import opt.LBFGSMinimizer;
import opt.Minimizer;
import sequence.ForwardBackward;
import tuple.Pair;
import arrays.a;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author jda
 */
public class CRFModel extends Model {

    private final double[] theta;
    private final Indexer<String> labelIndex;
    private final NodeFeaturizer nodeFeaturizer;
    private final EdgeFeaturizer edgeFeaturizer;
    private final Indexer<String> featureIndex;

    public CRFModel(double[] theta,
                    Indexer<String> labelIndex,
                    NodeFeaturizer nodeFeaturizer,
                    EdgeFeaturizer edgeFeaturizer,
                    Indexer<String> featureIndex) {
        this.theta = theta;
        this.labelIndex = labelIndex;
        this.nodeFeaturizer = nodeFeaturizer;
        this.edgeFeaturizer = edgeFeaturizer;
        this.featureIndex = featureIndex;
    }

    @Override
    public LabeledDatum predict(final Datum datum) {
        ForwardBackward.StationaryLattice lattice = new ForwardBackward.StationaryLattice() {
            @Override
            public int numSequences() {
                return 1;
            }

            @Override
            public int sequenceLength(int d) {
                return datum.tokenBoundaries.length;
            }

            @Override
            public int numStates(int d) {
                return labelIndex.size();
            }

            @Override
            public double nodeLogPotential(int d, int t, int s) {
                return dot(getIndices(nodeFeaturizer.apply(datum, s, t, datum.tokenBoundaries[t].beginFrame), featureIndex), theta);
            }

            @Override
            public double[] allowedEdgesLogPotentials(int d, int s, boolean backward) {
                double[] r = new double[numStates(d)];
                for (int i = 0; i < r.length; i++) {
                    int state1, state2;
                    state1 = backward ? i : s;
                    state2 = backward ? s : i;
                    r[i] = dot(getIndices(edgeFeaturizer.apply(datum, state1, state2), featureIndex), theta);
                }
                return r;
            }

            @Override
            public double nodePotential(int d, int t, int s) {
                return Math.exp(nodeLogPotential(d, t, s));
            }

            @Override
            public double[] allowedEdgesPotentials(int d, int s, boolean backward) {
                return a.exp(allowedEdgesLogPotentials(d, s, backward));
            }

            @Override
            public int[] allowedEdges(int d, int s, boolean backward) {
                return a.enumerate(0, numStates(d));
            }
        };

        Pair<ForwardBackward.NodeMarginals, ForwardBackward.StationaryEdgeMarginals> marginals =
                ForwardBackward.computeMarginalsLogSpace(lattice, new StateProjector(labelIndex.size()), false, 4);

        Token[] predicted = new Token[datum.tokenBoundaries.length];
        for (int t = 0; t < predicted.length; t++) {
            int predLabel = a.argmax(marginals.getFirst().nodeCondProbs(0, t));
            Token boundaryToken = datum.tokenBoundaries[t];
            Token predToken = new Token(labelIndex.getObject(predLabel), boundaryToken.beginFrame, boundaryToken.endFrame);
            predicted[t] = predToken;
        }
        return new LabeledDatum(datum, predicted, null);
    }

    public static CRFModel train(List<LabeledDatum> data, NodeFeaturizer nodeFeaturizer, EdgeFeaturizer edgeFeaturizer) {
        final Indexer<String> labelIndex = makeLabelIndex(data);
        final Indexer<String> featureIndex = makeFeatureIndex(data, labelIndex, nodeFeaturizer, edgeFeaturizer);
        final Pair<int[][][][], double[][][][]> nf = makeNodeFeatures(data, labelIndex.size(), nodeFeaturizer, featureIndex);
        final int[][][][] nodeFeatIds = nf.getFirst();
        final double[][][][] nodeFeatValues = nf.getSecond();
        final Pair<int[][][][], double[][][][]> ef = makeEdgeFeatures(data, labelIndex.size(), edgeFeaturizer, featureIndex);
        final int[][][][] edgeFeatIds = ef.getFirst();
        final double[][][][] edgeFeatValues = ef.getSecond();

        final DifferentiableFunction objective = makeObjective(data, labelIndex, nodeFeatIds, nodeFeatValues, edgeFeatIds, edgeFeatValues);
        double[] initWeights = a.zerosDouble(featureIndex.size());
        //EmpiricalGradientTester.test(objective, initWeights, 0.001, 1e-4, 1e-8);
        Minimizer minimizer = new LBFGSMinimizer(1e-6, 100);
        double[] weights = minimizer.minimize(objective, initWeights, true, new Minimizer.Callback() {
            @Override
            public void callback(double[] guess, int iter, double val, double[] grad) {
                //System.out.println("L = " + val);
            }
        });
        return new CRFModel(weights, labelIndex, nodeFeaturizer, edgeFeaturizer, featureIndex);
    }

    private static DifferentiableFunction makeObjective(final List<LabeledDatum> data,
                                                        final Indexer<String> labelIndex,
                                                        final int[][][][] nodeFeatIds,
                                                        final double[][][][] nodeFeatValues,
                                                        final int[][][][] edgeFeatIds,
                                                        final double[][][][] edgeFeatValues) {

        return new DifferentiableFunction() {

            @Override
            public Pair<Double, double[]> calculate(double[] theta) {
                ForwardBackward.StationaryLattice predictedLattice = new Lattice(nodeFeatIds, nodeFeatValues, edgeFeatIds, edgeFeatValues, theta);
                ForwardBackward.StationaryLattice observedLattice = new ObservedLattice(nodeFeatIds, nodeFeatValues, edgeFeatIds, edgeFeatValues, theta, data, labelIndex);
                ForwardBackward.StationaryStateProjector projector = new StateProjector(labelIndex.size());
                Pair<ForwardBackward.NodeMarginals, ForwardBackward.StationaryEdgeMarginals> observedMarginals =
                        ForwardBackward.computeMarginalsLogSpace(observedLattice, projector, false, 4);
                Pair<ForwardBackward.NodeMarginals, ForwardBackward.StationaryEdgeMarginals> predictedMarginals =
                        ForwardBackward.computeMarginalsLogSpace(predictedLattice, projector, false, 4);

                double likelihood = -1 * (observedMarginals.getFirst().logMarginalProb() - predictedMarginals.getFirst().logMarginalProb());
                double[] gradient = a.comb(expectedFeatures(theta, observedMarginals, nodeFeatIds, nodeFeatValues, edgeFeatIds, edgeFeatValues), -1d,  expectedFeatures(theta, predictedMarginals, nodeFeatIds, nodeFeatValues, edgeFeatIds, edgeFeatValues), 1d);

                likelihood += 0.5 * EcogExperiment.l2Regularizer * a.innerProd(theta, theta);
                a.combi(gradient, 1d, theta, EcogExperiment.l2Regularizer);

                return new Pair<Double, double[]>(likelihood, gradient);
            }

        };

    }

    public static Indexer<String> makeLabelIndex(List<LabeledDatum> data) {
        Indexer<String> indexer = new HashMapIndexer<String>();
        for (LabeledDatum datum : data) {
            for (Token tok : datum.labels) {
                indexer.getIndex(tok.label);
            }
        }
        indexer.lock();
        return indexer;
    }

    private static Indexer<String> makeFeatureIndex(List<LabeledDatum> data, Indexer<String> labelIndex, NodeFeaturizer nf, EdgeFeaturizer ef) {
        Indexer<String> indexer = new HashMapIndexer<String>();
        for (LabeledDatum datum : data) {
            for (int t = 0; t < datum.labels.length; t++) {
                int thisState = labelIndex.getIndex(datum.labels[t].label);
                // TODO(jda) could preallocate more intelligently
                indexer.index(nf.apply(datum, thisState, t, datum.labels[t].beginFrame).keySet().toArray(new String[0]));
                if (t < datum.labels.length - 1) {
                    int nextState = labelIndex.getIndex(datum.labels[t+1].label);
                    indexer.index(ef.apply(datum, thisState, nextState).keySet().toArray(new String[0]));
                }
            }
        }
        indexer.lock();
//        System.out.println(indexer.size());
//        System.exit(1);
        return indexer;
    }

    private static class Lattice implements ForwardBackward.StationaryLattice {

        final int[][][][] nodeFeatIds;
        final double[][][][] nodeFeatValues;
        final int[][][][] edgeFeatIds;
        final double[][][][] edgeFeatValues;
        final double[] theta;

        private Lattice(int[][][][] nodeFeatIds,
                        double[][][][] nodeFeatValues,
                        int[][][][] edgeFeatIds,
                        double[][][][] edgeFeatValues,
                        double[] theta) {
            this.nodeFeatIds = nodeFeatIds;
            this.nodeFeatValues = nodeFeatValues;
            this.edgeFeatIds = edgeFeatIds;
            this.edgeFeatValues = edgeFeatValues;
            this.theta = theta;
        }

        @Override
        public int numSequences() {
            return nodeFeatIds.length;
        }

        @Override
        public int sequenceLength(int d) {
            return nodeFeatIds[d].length;
        }

        @Override
        public int numStates(int d) {
            return edgeFeatIds[d].length;
        }

        @Override
        public double nodeLogPotential(int d, int t, int s) {
            return dot(nodeFeatIds[d][t][s], nodeFeatValues[d][t][s], theta);
        }

        @Override
        public double[] allowedEdgesLogPotentials(int d, int s, boolean backward) {
            double[] ret = new double[numStates(d)];
            for (int i = 0; i < ret.length; i++) {
                int[] features = backward ? edgeFeatIds[d][s][i] : edgeFeatIds[d][i][s];
                double[] values = backward ? edgeFeatValues[d][s][i] : edgeFeatValues[d][i][s];
                ret[i] = dot(features, values, theta);
            }
            return ret;
        }

        @Override
        public double nodePotential(int d, int t, int s) {
            return Math.exp(nodeLogPotential(d,t,s));
        }

        @Override
        public double[] allowedEdgesPotentials(int d, int s, boolean backward) {
            return a.exp(allowedEdgesLogPotentials(d, s, backward));
        }

        @Override
        public int[] allowedEdges(int d, int s, boolean backward) {
            return a.enumerate(0, numStates(d));
        }
    }

    private static class ObservedLattice extends Lattice {

        private final List<LabeledDatum> data;
        private final Indexer<String> labelIndex;

        private ObservedLattice(int[][][][] nodeFeatIds,
                                double[][][][] nodeFeatValues,
                                int[][][][] edgeFeatIds,
                                double[][][][] edgeFeatValues,
                                double[] theta, List<LabeledDatum> data,
                                Indexer<String> labelIndex) {
            super(nodeFeatIds, nodeFeatValues, edgeFeatIds, edgeFeatValues, theta);
            this.data = data;
            this.labelIndex = labelIndex;
        }

        // ASSUMES super.nodePotential calls nodeLogPotential!
        @Override
        public double nodeLogPotential(int d, int t, int s) {
            if (labelIndex.getIndex(data.get(d).labels[t].label) == s) {
                return super.nodeLogPotential(d, t, s);
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }

    }

    private static class StateProjector implements ForwardBackward.StationaryStateProjector {

        private int numLabels;

        private StateProjector(int numLabels) {
            this.numLabels = numLabels;
        }

        @Override
        public int domainSize(int d, int t) {
            return numLabels;
        }

        @Override
        public int rangeSize(int d) {
            return numLabels;
        }

        @Override
        public int project(int d, int t, int s) {
            return s;
        }
    }

    private static double[] expectedFeatures(double[] weights,
                                             Pair<ForwardBackward.NodeMarginals, ForwardBackward.StationaryEdgeMarginals> marginals,
                                             int[][][][] nodeFeatIds,
                                             double[][][][] nodeFeatValues,
                                             int[][][][] edgeFeatIds,
                                             double[][][][] edgeFeatValues) {
        double[] expectedWeights = new double[weights.length];
        ForwardBackward.NodeMarginals nodeMarginals = marginals.getFirst();
        ForwardBackward.StationaryEdgeMarginals edgeMarginals = marginals.getSecond();

        for (int d = 0; d < nodeMarginals.numSequences(); d++) {
            for (int t = 0; t < nodeMarginals.sequenceLength(d); t++) {
                for (int s = 0; s < nodeMarginals.numStates(d); s++) {
                    axpy(nodeMarginals.nodeCondProbs(d,t)[s], nodeFeatIds[d][t][s], nodeFeatValues[d][t][s], expectedWeights);
                }
            }
        }
        for (int d = 0; d < nodeMarginals.numSequences(); d++) {
            for (int s1 = 0; s1 < nodeMarginals.numStates(d); s1++) {
                for (int s2 = 0; s2 < nodeMarginals.numStates(d); s2++) {
//                    System.out.println(d + "," + s1 + "," + s2);
//                    System.out.println(edgeFeatures.length);
//                    System.out.println(edgeFeatures[0].length);
//                    System.out.println(edgeFeatures[0][0].length);
//                    System.out.println(edgeFeatures[0][0][0].length);
                    axpy(edgeMarginals.allowedForwardEdgesExpectedCounts(d, s1)[s2], edgeFeatIds[d][s1][s2], edgeFeatValues[d][s1][s2], expectedWeights);
                }
            }
        }
        return expectedWeights;
    }

    private static Pair<int[][][][], double[][][][]> makeNodeFeatures(List<LabeledDatum> data, int numStates, NodeFeaturizer nf, Indexer<String> featIndex) {
        int[][][][] ids = new int[data.size()][][][];
        double[][][][] values = new double[data.size()][][][];
        for (int d = 0; d < data.size(); d++) {
            LabeledDatum datum = data.get(d);
            ids[d] = new int[datum.labels.length][][];
            values[d] = new double[datum.labels.length][][];
            assert(datum.labels.length > 0);
            for (int t = 0; t < datum.labels.length; t++) {
                ids[d][t] = new int[numStates][];
                values[d][t] = new double[numStates][];
                for (int s = 0; s < numStates; s++) {
                    //System.out.println(nf.apply(datum, s, t, datum.labels[t].beginFrame));
                    Pair<int[],double[]> fv = getIndices(nf.apply(datum, s, t, datum.labels[t].beginFrame), featIndex);
                    ids[d][t][s] = fv.getFirst();
                    values[d][t][s] = fv.getSecond();
                    //ids[d][t][s] = getIndices(nf.apply(datum, s, t, datum.labels[t].beginFrame), featIndex);
                }
            }
        }
        return new Pair<int[][][][],double[][][][]>(ids, values);
    }

    private static Pair<int[][][][],double[][][][]> makeEdgeFeatures(List<LabeledDatum> data, int numStates, EdgeFeaturizer ef, Indexer<String> featIndex) {
        int[][][][] ids = new int[data.size()][][][];
        double[][][][] values = new double[data.size()][][][];
        for (int d = 0; d < data.size(); d++) {
            ids[d] = new int[numStates][numStates][];
            values[d] = new double[numStates][numStates][];
            for (int s1 = 0; s1 < numStates; s1++) {
                for (int s2 = 0; s2 < numStates; s2++) {
                    Pair<int[], double[]> fv = getIndices(ef.apply(data.get(d), s1, s2), featIndex);
                    ids[d][s1][s2] = fv.getFirst();
                    values[d][s1][s2] = fv.getSecond();
                    //features[d][s1][s2] = getIndices(ef.apply(data.get(d), s1, s2), featIndex);
                }
            }
        }
        return new Pair<int[][][][],double[][][][]>(ids, values);
    }

//    private static int[] getIndices(String[] feats, Indexer<String> index) {
//        List<Integer> r = new ArrayList<Integer>(feats.length);
//        for (String feat : feats) {
//            if (index.contains(feat)) {
//                r.add(index.getIndex(feat));
//            }
//        }
//        int[] rr = new int[r.size()];
//        for (int i = 0; i < r.size(); i++) {
//            rr[i] = r.get(i);
//        }
//        return rr;
//    }

    private static Pair<int[], double[]> getIndices(Counter<String> feats, Indexer<String> index) {
        List<Integer> ids = new ArrayList<Integer>(feats.size());
        List<Double> values = new ArrayList<Double>(feats.size());
        for (Map.Entry<String,Double> e : feats.entries()) {
            if (index.contains(e.getKey())) {
                ids.add(index.getIndex(e.getKey()));
                values.add(e.getValue());
            }
        }
        int[] rI = new int[ids.size()];
        double[] rV = new double[values.size()];
        for (int i = 0; i < ids.size(); i++) {
            rI[i] = ids.get(i);
            rV[i] = values.get(i);
        }
        return new Pair<int[], double[]>(rI, rV);
    }

    private static double dot(int[] featIds, double[] featValues, double[] theta) {
        double r = 0;
        for (int i = 0; i < featIds.length; i++) {
            r += theta[featIds[i]] * featValues[i];
        }
        return r;
    }

    private static double dot(Pair<int[],double[]> fv, double[] theta) {
        return dot(fv.getFirst(), fv.getSecond(), theta);
    }

    private static void axpy(double a, int[] xIds, double[] xVals, double[] y) {
        for (int i = 0; i < xIds.length; i++) {
            y[xIds[i]] += a * xVals[i];
        }
    }
}

