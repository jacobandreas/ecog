package ecog.main;

import classifier.LibLinearWrapper;
import de.bwaldvogel.liblinear.SolverType;
import ecog.data.Dataset;
import ecog.eval.EvalStats;
import ecog.features.*;
import ecog.model.CRFModel;
import ecog.model.IndepClassifierModel;
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
    public static double l2Regularizer = 10;
    // .202
    // .158

    @Option(gloss = "phone transition model training path")
    public static String phonePath;

    @Option(gloss = "CMU/TIMIT phone symbol correspondences")
    public static String phoneMapPath;

    public void run() {
        Dataset data = Dataset.load();
        // TODO(jda) duplicate index is wasteful
        System.out.println("Dataset loaded");
        NodeFeaturizer nf = new SimpleNodeFeaturizer();
        EdgeFeaturizer ef = new CMUEdgeFeaturizer(phonePath, phoneMapPath, CRFModel.makeLabelIndex(data.train));
        Model model = CRFModel.train(data.train, nf, ef);

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
