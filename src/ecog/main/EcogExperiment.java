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
        System.out.println("Dataset loaded");
        
//        NodeFeaturizer nf = new SimpleNodeFeaturizer();
//        NodeFeaturizer nf = new FancyNodeFeaturizer();
//        EdgeFeaturizer ef = new CMUEdgeFeaturizer(phonePath, phoneMapPath, CRFModel.makeLabelIndex(data.train));
//        Model model = CRFModel.train(data.train, nf, ef);

//      Model model = KNNModel.train(data.train, new KNNModel.FixedWidthSimilarityMetric(20, 10), 9); // good for resp
//      Model model = KNNModel.train(data.train, new KNNModel.FixedWidthSimilarityMetric(10, -5), 9); // good for mel
//      Model model = MostCommonModel.train(data.train);
      
//      Model model = IndepClassifierModel.train(data.train, new LibLinearWrapper(SolverType.MCSVM_CS, 1e4, 1e-4), 
//      		new IndepClassifierModel.IndexingFeatureExtractor(
//      			new IndepClassifierModel.SegmentStatsFeatureExtractor(),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.SegmentStatsFeatureExtractor(), -0.5, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.SegmentStatsFeatureExtractor(), 0.0, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.SegmentStatsFeatureExtractor(), 0.5, data.train),
//      			new IndepClassifierModel.WindowStatsFeatureExtractor(5, -5),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, -5), -0.5, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, -5), 0.0, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, -5), 0.5, data.train),
//      			new IndepClassifierModel.WindowStatsFeatureExtractor(5, 0),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 0), -0.5, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 0), 0.0, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 0), 0.5, data.train),
//      			new IndepClassifierModel.WindowStatsFeatureExtractor(5, 5),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 5), -0.5, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 5), 0.0, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 5), 0.5, data.train),
//      			new IndepClassifierModel.WindowStatsFeatureExtractor(5, 10),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 10), -0.5, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 10), 0.0, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 10), 0.5, data.train),
//      			new IndepClassifierModel.WindowStatsFeatureExtractor(20, -5),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(20, -5), -0.5, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(20, -5), 0.0, data.train),
//      			new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(20, -5), 0.5, data.train)));
      
      Model model = IndepClassifierModel.train(data.train, new LibLinearWrapper(SolverType.MCSVM_CS, 1e0, 1e-5), 
    		  new IndepClassifierModel.IndexingFeatureExtractor(
    				  new IndepClassifierModel.SegmentStatsFeatureExtractor(),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.SegmentStatsFeatureExtractor(), -0.5, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.SegmentStatsFeatureExtractor(), 0.0, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.SegmentStatsFeatureExtractor(), 0.5, data.train),
    				  new IndepClassifierModel.WindowStatsFeatureExtractor(5, 10),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 10), -0.5, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 10), 0.0, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 10), 0.5, data.train),
    				  new IndepClassifierModel.WindowStatsFeatureExtractor(5, 15),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 15), -0.5, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 15), 0.0, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 15), 0.5, data.train),
    				  new IndepClassifierModel.WindowStatsFeatureExtractor(5, 20),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 20), -0.5, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 20), 0.0, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 20), 0.5, data.train),
    				  new IndepClassifierModel.WindowStatsFeatureExtractor(5, 25),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 25), -0.5, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 25), 0.0, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 25), 0.5, data.train),
    				  new IndepClassifierModel.WindowStatsFeatureExtractor(20, 10),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(20, 10), -0.5, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(20, 10), 0.0, data.train),
    				  new IndepClassifierModel.BinarizingFeatureExtractor(new IndepClassifierModel.WindowStatsFeatureExtractor(20, 10), 0.5, data.train)));

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
