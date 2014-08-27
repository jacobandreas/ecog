package ecog.model;

import indexer.HashMapIndexer;
import indexer.Indexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import threading.BetterThreader;
import tuple.Pair;
import counter.Counter;
import counter.CounterInterface;
import counter.IntCounter;
import classifier.Classifier;
import ecog.data.Datum;
import ecog.data.LabeledDatum;
import ecog.data.Token;

public class IndepClassifierModel extends Model {
	
	public static interface FeatureExtractor {
		public CounterInterface<Integer> extract(Datum datum, int tokenIndex);
	}
	
	public static interface StringFeatureExtractor {
		public CounterInterface<String> extract(Datum datum, int tokenIndex);
	}
	
	public static class SegmentStatsFeatureExtractor implements StringFeatureExtractor {
		public CounterInterface<String> extract(Datum datum, int tokenIndex) {
			CounterInterface<String> features = new Counter<String>();
			
			double[][] signal = datum.response;
//			double[][] signal = datum.mel;
			
			int beginFrame = datum.tokenBoundaries[tokenIndex].beginFrame;
			int endFrame = datum.tokenBoundaries[tokenIndex].endFrame;
			
			double numFrames = endFrame - beginFrame;
			int numDim = signal[0].length;
			
			for (int d=0; d<numDim; ++d) {
				double mean = 0.0;
				for (int f=beginFrame; f<endFrame; ++f) {
					mean += signal[f][d] / numFrames;
				}
				double var = 0.0;
				for (int f=beginFrame; f<endFrame; ++f) {
					double meanDiff = mean - datum.response[f][d];
					var += meanDiff * meanDiff / numFrames;
				}
				double min = Double.POSITIVE_INFINITY;
				double max = Double.NEGATIVE_INFINITY;
				for (int f=beginFrame; f<endFrame; ++f) {
					min = Math.min(min, datum.response[f][d]);
					max = Math.max(max, datum.response[f][d]);
				}
				
				features.setCount(d+"_mean_segstats", mean);
				features.setCount(d+"_var_segstats", var);
				features.setCount(d+"_min_segstats", min);
				features.setCount(d+"_max_segstats", max);
			}
			return features;
		}
	}
	
	public static class WindowStatsFeatureExtractor implements StringFeatureExtractor {
		int width;
		int offset;
		
		public WindowStatsFeatureExtractor(int width, int offset) {
			this.width = width;
			this.offset = offset;
		}
		
		public CounterInterface<String> extract(Datum datum, int tokenIndex) {
			CounterInterface<String> features = new Counter<String>();
			
			double[][] signal = datum.response;
//			double[][] signal = datum.mel;
			
			int beginFrame = datum.tokenBoundaries[tokenIndex].beginFrame + offset;
//			int beginFrame = (datum.tokenBoundaries[tokenIndex].beginFrame+datum.tokenBoundaries[tokenIndex].endFrame)/2 + offset;
			int endFrame = beginFrame + width;
			
			double numFrames = endFrame - beginFrame;
			int numDim = signal[0].length;
			
			for (int d=0; d<numDim; ++d) {
				double mean = 0.0;
				for (int f=beginFrame; f<endFrame; ++f) {
					if (f >=0 && f<signal.length) {
						mean += signal[f][d] / numFrames;
					}
				}
				double var = 0.0;
				for (int f=beginFrame; f<endFrame; ++f) {
					if (f >=0 && f<signal.length) {
						double meanDiff = mean - signal[f][d];
						var += meanDiff * meanDiff / numFrames;
					}
				}
				double min = Double.POSITIVE_INFINITY;
				double max = Double.NEGATIVE_INFINITY;
				for (int f=beginFrame; f<endFrame; ++f) {
					if (f >=0 && f<signal.length) {
						min = Math.min(min, signal[f][d]);
						max = Math.max(max, signal[f][d]);
					}
				}
				
				features.setCount(d+"_mean_width"+width+"_offset"+offset+"_winstats", mean);
				features.setCount(d+"_var_width"+width+"_offset"+offset+"_winstats", var);
				features.setCount(d+"_min_width"+width+"_offset"+offset+"_winstats", min);
				features.setCount(d+"_max_width"+width+"_offset"+offset+"_winstats", max);
			}
			return features;
		}
	}
	
	public static class WindowIdentityFeatureExtractor implements StringFeatureExtractor {
		int width;
		int offset;
		
		public WindowIdentityFeatureExtractor(int width, int offset) {
			this.width = width;
			this.offset = offset;
		}
		
		public CounterInterface<String> extract(Datum datum, int tokenIndex) {
			CounterInterface<String> features = new Counter<String>();
			
			double[][] signal = datum.response;
//			double[][] signal = datum.mel;
			
			int beginFrame = datum.tokenBoundaries[tokenIndex].beginFrame + offset;
//			int beginFrame = (datum.tokenBoundaries[tokenIndex].beginFrame+datum.tokenBoundaries[tokenIndex].endFrame)/2 + offset;
			int endFrame = beginFrame + width;
			
			int numDim = signal[0].length;
			
			for (int f=beginFrame; f<endFrame; ++f) {
				if (f >=0 && f<signal.length) {
					for (int d=0; d<numDim; ++d) {
						features.setCount(d+"_"+(f-beginFrame)+"_width"+width+"_offset"+offset+"_winident", signal[f][d]);
					}
				}
			}
			
			return features;
		}
	}
	
	public static class IndexingFeatureExtractor implements FeatureExtractor {
		Indexer<String> featureIndexer;
		StringFeatureExtractor[] featureExtractors;
		
		public IndexingFeatureExtractor(StringFeatureExtractor ... featureExtractors) {
			this.featureExtractors = featureExtractors;
			this.featureIndexer = new HashMapIndexer<String>();
		}
		
		public CounterInterface<Integer> extract(Datum datum, int tokenIndex) {
			CounterInterface<Integer> features = new IntCounter();
			features.setCount(featureIndexer.getIndex("bias"), 1.0);
			for (StringFeatureExtractor extractor : featureExtractors) {
				CounterInterface<String> stringFeatures = extractor.extract(datum, tokenIndex);
				for (Map.Entry<String,Double> entry : stringFeatures.entries()) {
					int featureIndex = featureIndexer.getIndex(entry.getKey());
					double val = entry.getValue();
					features.incrementCount(featureIndex, val);
				}
			}
			return features;
		}
	}
	
	Indexer<String> labelIndexer;
	FeatureExtractor featureExtractor;
	Classifier classifier;

	public IndepClassifierModel(List<LabeledDatum> data, Classifier classifier, FeatureExtractor featureExtractor) {
		this.classifier = classifier;
		this.featureExtractor = featureExtractor;
		this.labelIndexer = new HashMapIndexer<String>();
		List<Pair<CounterInterface<Integer>,Integer>> trainSet = new ArrayList<Pair<CounterInterface<Integer>,Integer>>();
		for (LabeledDatum labDatum : data) {
			for (int tokenIndex=0; tokenIndex<labDatum.labels.length; ++tokenIndex) {
				int label = labelIndexer.getIndex(labDatum.labels[tokenIndex].label);
				CounterInterface<Integer> features = featureExtractor.extract(labDatum.wrapped, tokenIndex);
				trainSet.add(Pair.makePair(features, label));
			}
		}
		this.labelIndexer.lock();
		this.classifier.train(trainSet);
	}
	
	public static Model train(List<LabeledDatum> data, Classifier classifier, FeatureExtractor featureExtractor) {
		return new IndepClassifierModel(data, classifier, featureExtractor);
	}
	
	public LabeledDatum predict(Datum datum) {
		final Token[] predictedPhones = new Token[datum.tokenBoundaries.length];
		BetterThreader.Function<Integer,Object> func = new BetterThreader.Function<Integer,Object>(){public void call(Integer tokenIndex, Object ignore){
			predictedPhones[tokenIndex] = predict(datum, tokenIndex);
		}};
		BetterThreader<Integer,Object> threader = new BetterThreader<Integer,Object>(func, 8);
		for (int tokenIndex=0; tokenIndex<predictedPhones.length; ++tokenIndex) threader.addFunctionArgument(tokenIndex);
		threader.run();
		return new LabeledDatum(datum, predictedPhones, null);
	}
	
	private Token predict(Datum datum, int tokenIndex) {
		String label = labelIndexer.getObject(classifier.predict(featureExtractor.extract(datum, tokenIndex)));
		return new Token(label, datum.tokenBoundaries[tokenIndex].beginFrame, datum.tokenBoundaries[tokenIndex].endFrame);
	}

}
