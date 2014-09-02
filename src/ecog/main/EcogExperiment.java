package ecog.main;

import java.util.HashSet;
import java.util.Set;

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

	  Set<String> vowels = new HashSet<String>();
	  vowels.add("aa");
	  vowels.add("ae");
	  vowels.add("ax");
	  vowels.add("ah");
	  vowels.add("ax-h");
	  vowels.add("ao");
	  vowels.add("eh");
	  vowels.add("ih");
	  vowels.add("ix");
	  vowels.add("iy");
	  vowels.add("uh");
	  vowels.add("uw");
	  vowels.add("ay");
	  vowels.add("aw");
	  vowels.add("ey");
	  vowels.add("ow");
	  vowels.add("oy");
	  vowels.add("er");
	  Set<String> consonants = new HashSet<String>();
	  consonants.add("ng");
	  consonants.add("eng");
	  consonants.add("sh");
	  consonants.add("ch");
	  consonants.add("y");
	  consonants.add("zh");
	  consonants.add("jh");
	  consonants.add("dh");
	  consonants.add("hh");
	  consonants.add("hv");
	  consonants.add("th");
	  consonants.add("b");
	  consonants.add("bcl");
	  consonants.add("d");
	  consonants.add("dcl");
	  consonants.add("f");
	  consonants.add("g");
	  consonants.add("gcl");
	  consonants.add("k");
	  consonants.add("kcl");
	  consonants.add("l");
	  consonants.add("el");
	  consonants.add("m");
	  consonants.add("em");
	  consonants.add("n");
	  consonants.add("en");
	  consonants.add("nx");
	  consonants.add("p");
	  consonants.add("pcl");
	  consonants.add("r");
	  consonants.add("s");
	  consonants.add("t");
	  consonants.add("tcl");
	  consonants.add("dx");
	  consonants.add("v");
	  consonants.add("w");
	  consonants.add("y");
	  consonants.add("z");
	  consonants.add("epi");
	  consonants.add("h#");
	  consonants.add("pau");
	  consonants.add("q");
	  Set<String> all = new HashSet<String>();
	  all.addAll(vowels);
	  all.addAll(consonants);
      
      {   
    	  EvalStats trainEval = model.evaluate(data.train);
    	  System.out.println("train: " + trainEval);
    	  EvalStats devEval = model.evaluate(data.dev);
    	  System.out.println("dev: " + devEval);
      }
      {   
    	  EvalStats trainEval = model.evaluateLabelSubset(data.train, vowels);
    	  System.out.println("vowels train: " + trainEval);
    	  EvalStats devEval = model.evaluateLabelSubset(data.dev, vowels);
    	  System.out.println("vowels dev: " + devEval);
      }
      {   
    	  EvalStats trainEval = model.evaluateLabelSubset(data.train, consonants);
    	  System.out.println("consonants train: " + trainEval);
    	  EvalStats devEval = model.evaluateLabelSubset(data.dev, consonants);
    	  System.out.println("consonants dev: " + devEval);
      }
      for (String phone : all) {
    	  Set<String> singletonPhone = new HashSet<String>();
    	  singletonPhone.add(phone);
    	  EvalStats trainEval = model.evaluateLabelSubset(data.train, singletonPhone);
    	  System.out.println(phone+" train: " + trainEval);
    	  EvalStats devEval = model.evaluateLabelSubset(data.dev, singletonPhone);
    	  System.out.println(phone+" consonants dev: " + devEval);
      }
    }

    public static void main(String[] args) {
        EcogExperiment experiment = new EcogExperiment();
        Execution.run(args, experiment);
    }
}
