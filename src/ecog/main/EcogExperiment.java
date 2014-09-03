package ecog.main;

import java.util.HashSet;
import java.util.Set;

import tuple.Pair;
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
	  
	  Set<String> stops = new HashSet<String>();
	  stops.add("p");
	  stops.add("pcl");
	  stops.add("b");
	  stops.add("bcl");
	  stops.add("t");
	  stops.add("tcl");
	  stops.add("dx");
	  stops.add("d");
	  stops.add("dcl");
	  stops.add("k");
	  stops.add("kcl");
	  stops.add("g");
	  stops.add("gcl");
	  
	  Set<String> dentals = new HashSet<String>();
	  dentals.add("d");
	  dentals.add("dcl");
	  dentals.add("t");
	  dentals.add("tcl");
	  dentals.add("dx");
	  dentals.add("n");
	  dentals.add("l");
	  
	  Set<String> voiced = new HashSet<String>();
	  voiced.add("m");
	  voiced.add("b");
	  voiced.add("bcl");
	  voiced.add("v");
	  voiced.add("dh");
	  voiced.add("n");
	  voiced.add("d");
	  voiced.add("dcl");
	  voiced.add("z");
	  voiced.add("l");
	  voiced.add("jh");
	  voiced.add("zh");
	  voiced.add("ng");
	  voiced.add("g");
	  voiced.add("gcl");
	  voiced.add("w");
	  
	  Set<String> unvoiced = new HashSet<String>();
	  unvoiced.add("p");
	  unvoiced.add("pcl");
	  unvoiced.add("f");
	  unvoiced.add("th");
	  unvoiced.add("t");
	  unvoiced.add("tcl");
	  unvoiced.add("s");
	  unvoiced.add("ch");
	  unvoiced.add("sh");
	  unvoiced.add("k");
	  unvoiced.add("kcl");
	  unvoiced.add("h");
	  
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
    	  Pair<EvalStats,EvalStats> trainEval = model.evaluateLabelSubset(data.train, stops);
    	  System.out.println("stops train prec: " + trainEval.getFirst());
    	  System.out.println("stops train recall: " + trainEval.getSecond());
    	  Pair<EvalStats,EvalStats> devEval = model.evaluateLabelSubset(data.dev, stops);
    	  System.out.println("stops dev prec: " + devEval.getFirst());
    	  System.out.println("stops dev recall: " + devEval.getSecond());
      }
      {   
    	  Pair<EvalStats,EvalStats> trainEval = model.evaluateLabelSubset(data.train, dentals);
    	  System.out.println("dentals train prec: " + trainEval.getFirst());
    	  System.out.println("dentals train recall: " + trainEval.getSecond());
    	  Pair<EvalStats,EvalStats> devEval = model.evaluateLabelSubset(data.dev, dentals);
    	  System.out.println("dentals dev prec: " + devEval.getFirst());
    	  System.out.println("dentals dev recall: " + devEval.getSecond());
      }
      {   
    	  Pair<EvalStats,EvalStats> trainEval = model.evaluateLabelSubset(data.train, voiced);
    	  System.out.println("voiced train prec: " + trainEval.getFirst());
    	  System.out.println("voiced train recall: " + trainEval.getSecond());
    	  Pair<EvalStats,EvalStats> devEval = model.evaluateLabelSubset(data.dev, voiced);
    	  System.out.println("voiced dev prec: " + devEval.getFirst());
    	  System.out.println("voiced dev recall: " + devEval.getSecond());
      }
      {   
    	  Pair<EvalStats,EvalStats> trainEval = model.evaluateLabelSubset(data.train, unvoiced);
    	  System.out.println("unvoiced train prec: " + trainEval.getFirst());
    	  System.out.println("unvoiced train recall: " + trainEval.getSecond());
    	  Pair<EvalStats,EvalStats> devEval = model.evaluateLabelSubset(data.dev, unvoiced);
    	  System.out.println("unvoiced dev prec: " + devEval.getFirst());
    	  System.out.println("unvoiced dev recall: " + devEval.getSecond());
      }
      {   
    	  Pair<EvalStats,EvalStats> trainEval = model.evaluateLabelSubset(data.train, vowels);
    	  System.out.println("vowels train prec: " + trainEval.getFirst());
    	  System.out.println("vowels train recall: " + trainEval.getSecond());
    	  Pair<EvalStats,EvalStats> devEval = model.evaluateLabelSubset(data.dev, vowels);
    	  System.out.println("vowels dev prec: " + devEval.getFirst());
    	  System.out.println("vowels dev recall: " + devEval.getSecond());
      }
      {   
    	  Pair<EvalStats,EvalStats> trainEval = model.evaluateLabelSubset(data.train, consonants);
    	  System.out.println("consonants train prec: " + trainEval.getFirst());
    	  System.out.println("consonants train recall: " + trainEval.getSecond());
    	  Pair<EvalStats,EvalStats> devEval = model.evaluateLabelSubset(data.dev, consonants);
    	  System.out.println("consonants dev prec: " + devEval.getFirst());
    	  System.out.println("consonants dev recall: " + devEval.getSecond());
      }
      for (String phone : all) {
    	  Set<String> singletonPhone = new HashSet<String>();
    	  singletonPhone.add(phone);
    	  Pair<EvalStats,EvalStats> trainEval = model.evaluateLabelSubset(data.train, singletonPhone);
    	  System.out.println(phone+" train prec: " + trainEval.getFirst());
    	  System.out.println(phone+" train recall: " + trainEval.getSecond());
    	  Pair<EvalStats,EvalStats> devEval = model.evaluateLabelSubset(data.dev, singletonPhone);
    	  System.out.println(phone+" dev prec: " + devEval.getFirst());
    	  System.out.println(phone+" dev recall: " + devEval.getSecond());
      }
    }

    public static void main(String[] args) {
        EcogExperiment experiment = new EcogExperiment();
        Execution.run(args, experiment);
    }
}
