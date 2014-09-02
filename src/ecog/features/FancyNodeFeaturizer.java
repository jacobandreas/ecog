package ecog.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import counter.Counter;
import counter.CounterInterface;
import ecog.data.Datum;
import ecog.model.IndepClassifierModel;
import ecog.model.IndepClassifierModel.StringFeatureExtractor;

public class FancyNodeFeaturizer implements NodeFeaturizer {

	List<StringFeatureExtractor> featurizers ;
	
	public FancyNodeFeaturizer() {
		featurizers = new ArrayList<StringFeatureExtractor>();
		featurizers.add(new IndepClassifierModel.SegmentStatsFeatureExtractor());
		featurizers.add(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 10));
		featurizers.add(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 15));
		featurizers.add(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 20));
		featurizers.add(new IndepClassifierModel.WindowStatsFeatureExtractor(5, 25));
		featurizers.add(new IndepClassifierModel.WindowStatsFeatureExtractor(20, 10));
	}
	
	
	public Counter<String> apply(Datum datum, int state, int t, int frame) {
		 Counter<String> feats = new Counter<String>();
		 for (StringFeatureExtractor featurizer : featurizers) {
			 CounterInterface<String> stateFeats = featurizer.extract(datum, t);
			 for (Map.Entry<String,Double> entry : stateFeats.entries()) {
				 feats.incrementCount(entry.getKey()+"_state"+state, entry.getValue());
			 }
		 }
        return feats;
    }
}
