package ecog.model;

import ecog.data.Datum;
import ecog.data.LabeledDatum;
import ecog.data.Token;
import ecog.eval.EvalStats;
import indexer.HashMapIndexer;
import indexer.Indexer;
import opt.DifferentiableFunction;
import opt.EmpiricalGradientTester;
import opt.LBFGSMinimizer;
import opt.Minimizer;
import sequence.ForwardBackward;
import tuple.Pair;
import arrays.a;

import java.util.List;
import java.util.Random;

/**
 * @author jda
 */
public class CRFModel implements Model {

    private final double[] weights;

    public CRFModel(double[] weights) {
        this.weights = weights;
    }

    @Override
    public LabeledDatum predict(Datum datum) {
        return null;
    }

    @Override
    public EvalStats evaluate(List<LabeledDatum> data) {
        return null;
    }

    public static CRFModel train(List<LabeledDatum> data) {
        //data = data.subList(0, 20);
        final Indexer<String> labelIndex = makeLabelIndex(data);
        final int[][][][] nodeFeatures = makeNodeFeatures(data, labelIndex.size());
        final int[][][][] edgeFeatures = makeEdgeFeatures(data, labelIndex.size());

        final DifferentiableFunction objective = makeObjective(data, labelIndex, nodeFeatures, edgeFeatures);
        double[] initWeights = a.randDouble(3 * (int) Math.pow(labelIndex.size(), 2), new Random());
        //EmpiricalGradientTester.test(objective, initWeights, 0.001, 1e-4, 1e-8);
        Minimizer minimizer = new LBFGSMinimizer(1e-5, 100);
        double[] weights = minimizer.minimize(objective, initWeights, true, new Minimizer.Callback() {
            @Override
            public void callback(double[] guess, int iter, double val, double[] grad) {
            }
        });
        return new CRFModel(weights);
    }

    private static DifferentiableFunction makeObjective(final List<LabeledDatum> data, final Indexer<String> labelIndex, final int[][][][] nodeFeatures, final int[][][][] edgeFeatures) {

        return new DifferentiableFunction() {

            @Override
            public Pair<Double, double[]> calculate(double[] theta) {
                ForwardBackward.StationaryLattice predictedLattice = new Lattice(nodeFeatures, edgeFeatures, theta);
                ForwardBackward.StationaryLattice observedLattice = new ObservedLattice(nodeFeatures, edgeFeatures, theta, data, labelIndex);
                ForwardBackward.StationaryStateProjector projector = new StateProjector(labelIndex.size());
                Pair<ForwardBackward.NodeMarginals, ForwardBackward.StationaryEdgeMarginals> observedMarginals =
                        ForwardBackward.computeMarginalsLogSpace(observedLattice, projector, false, 4);
                Pair<ForwardBackward.NodeMarginals, ForwardBackward.StationaryEdgeMarginals> predictedMarginals =
                        ForwardBackward.computeMarginalsLogSpace(predictedLattice, projector, false, 4);
                double likelihood = -1 * (observedMarginals.getFirst().logMarginalProb() - predictedMarginals.getFirst().logMarginalProb());
                double[] gradient = a.comb(expectedFeatures(theta, observedMarginals, nodeFeatures, edgeFeatures), -1d,  expectedFeatures(theta, predictedMarginals, nodeFeatures, edgeFeatures), 1d);

                return new Pair<Double, double[]>(likelihood, gradient);
            }

        };

    }

    private static Indexer<String> makeLabelIndex(List<LabeledDatum> data) {
        Indexer<String> indexer = new HashMapIndexer<String>();
        for (LabeledDatum datum : data) {
            for (Token tok : datum.labels) {
                indexer.getIndex(tok.label);
            }
        }
        indexer.lock();
        return indexer;
    }

    private static class Lattice implements ForwardBackward.StationaryLattice {

        final int[][][][] nodeFeatures;
        final int[][][][] edgeFeatures;
        final double[] theta;

        private Lattice(int[][][][] nodeFeatures, int[][][][] edgeFeatures, double[] theta) {
            this.nodeFeatures = nodeFeatures;
            this.edgeFeatures = edgeFeatures;
            this.theta = theta;
        }

        @Override
        public int numSequences() {
            return nodeFeatures.length;
        }

        @Override
        public int sequenceLength(int d) {
            return nodeFeatures[d].length;
        }

        @Override
        public int numStates(int d) {
            return edgeFeatures[d].length;
        }

        @Override
        public double nodeLogPotential(int d, int t, int s) {
            return dot(nodeFeatures[d][t][s], theta);
        }

        @Override
        public double[] allowedEdgesLogPotentials(int d, int s, boolean backward) {
            double[] ret = new double[numStates(d)];
            for (int i = 0; i < ret.length; i++) {
                int[] features = backward ? edgeFeatures[d][s][i] : edgeFeatures[d][i][s];
                ret[i] = dot(features, theta);
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

        private ObservedLattice(int[][][][] nodeFeatures, int[][][][] edgeFeatures, double[] theta, List<LabeledDatum> data, Indexer<String> labelIndex) {
            super(nodeFeatures, edgeFeatures, theta);
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

    private static double[] expectedFeatures(double[] weights, Pair<ForwardBackward.NodeMarginals, ForwardBackward.StationaryEdgeMarginals> marginals, int[][][][] nodeFeatures, int[][][][] edgeFeatures) {
        double[] expectedWeights = new double[weights.length];
        ForwardBackward.NodeMarginals nodeMarginals = marginals.getFirst();
        ForwardBackward.StationaryEdgeMarginals edgeMarginals = marginals.getSecond();

        for (int d = 0; d < nodeMarginals.numSequences(); d++) {
            for (int t = 0; t < nodeMarginals.sequenceLength(d); t++) {
                for (int s = 0; s < nodeMarginals.numStates(d); s++) {
                    axpy(nodeMarginals.nodeCondProbs(d,t)[s], nodeFeatures[d][t][s], expectedWeights);
                }
            }
        }
        for (int d = 0; d < nodeMarginals.numSequences(); d++) {
            for (int s1 = 0; s1 < nodeMarginals.numStates(d); s1++) {
                for (int s2 = 0; s2 < nodeMarginals.numStates(d); s2++) {
                    axpy(edgeMarginals.allowedForwardEdgesExpectedCounts(d, s1)[s2], edgeFeatures[d][s1][s2], expectedWeights);
                }
            }
        }
        return expectedWeights;
    }

    private static int[][][][] makeNodeFeatures(List<LabeledDatum> data, int numStates) {
        int[][][][] features = new int[data.size()][][][];
        for (int d = 0; d < data.size(); d++) {
            LabeledDatum datum = data.get(d);
            features[d] = new int[datum.labels.length][][];
            for (int t = 0; t < datum.labels.length; t++) {
                features[d][t] = new int[numStates][];
                for (int s = 0; s < numStates; s++) {
                    features[d][t][s] = new int[] { s };
                }
            }
        }
        return features;
    }

    private static int[][][][] makeEdgeFeatures(List<LabeledDatum> data, int numStates) {
        int[][][][] features = new int[data.size()][][][];
        for (int d = 0; d < data.size(); d++) {
            features[d] = new int[numStates][numStates][];
            for (int s1 = 0; s1 < numStates; s1++) {
                for (int s2 = 0; s2 < numStates; s2++) {
                    features[d][s1][s2] = new int[] { numStates + s1 * numStates + s2 };
                }
            }
        }
        return features;
    }

    private static double dot(int[] features, double[] theta) {
        double r = 0;
        for (int f : features) {
            r += theta[f];
        }
        return r;
    }

    private static void axpy(double a, int[] x, double[] y) {
        for (int f : x) {
            y[f] += a;
        }
    }
}

