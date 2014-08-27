package ecog.main;

import ecog.data.Dataset;
import ecog.eval.EvalStats;
import ecog.features.HackyKernelFeaturizer;
import ecog.features.SimpleEdgeFeaturizer;
import ecog.features.SimpleNodeFeaturizer;
import ecog.model.CRFModel;
import ecog.model.KNNModel;
import ecog.model.Model;
import ecog.model.MostCommonModel;
import fig.Execution;
import fig.Option;

/**
 * @author jda
 */
public class EcogExperiment implements Runnable {

    @Option(gloss = "path to ECOG data")
    public static String dataRoot;

    @Option(gloss = "path to patient (e.g. 'left_grid/EC36')")
    public static String patient = "left_grid/EC36";

    @Option(gloss = "number of recordings to run on")
    public static int nRecordings = Integer.MAX_VALUE;

    @Option(gloss = "l2 regularization strength")
    public static double l2Regularizer = .01;

    public void run() {
        Dataset data = Dataset.load();
        // TODO(jda) duplicate index is wasteful
        //Model model = CRFModel.train(data.train, new HackyKernelFeaturizer(data.train.subList(0, 100), 5000, CRFModel.makeLabelIndex(data.train)), new SimpleEdgeFeaturizer());
        System.out.println("Dataset loaded");
//        Model model = CRFModel.train(data.train, new SimpleNodeFeaturizer(), new SimpleEdgeFeaturizer());
        Model model = KNNModel.train(data.train, new KNNModel.FixedWidthSimilarityMetric(20, 10), 9);
//        Model model = KNNModel.train(data.train, new KNNModel.FixedWidthSimilarityMetric(10, -5), 9);
//        Model model = MostCommonModel.train(data.train);
        EvalStats trainEval = model.evaluate(data.train);
        System.out.println("train: " + trainEval);
        EvalStats devEval = model.evaluate(data.dev);
        System.out.println("dev: " + devEval);
    }

    public static void main(String[] args) {
        EcogExperiment experiment = new EcogExperiment();
        Execution.run(args, experiment);
    }
}
