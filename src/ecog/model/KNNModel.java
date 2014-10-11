package ecog.model;

import java.util.List;

import arrays.a;
import counter.Counter;
import threading.BetterThreader;
import tuple.Pair;
import util.PriorityQueue;
import ecog.data.Datum;
import ecog.data.LabeledDatum;
import ecog.data.Token;

public class KNNModel extends Model {
	
	public static interface SimilarityMetric {
		public double similarity(Datum datum1, int tokenIndex1, Datum datum2, int tokenIndex2);
	}
	
	public static class WindowSimilarityMetric implements SimilarityMetric {
		int width;
		int offset;
		
		public WindowSimilarityMetric(int width, int offset) {
			this.width = width;
			this.offset = offset;
		}
		
		public double similarity(Datum datum1, int tokenIndex1, Datum datum2, int tokenIndex2) {
			float score = 0.0f;
			float count = 0.0f;
			
			// good for resp
			int beginFrame1 = datum1.tokenBoundaries[tokenIndex1].beginFrame + offset;
			int beginFrame2 = datum2.tokenBoundaries[tokenIndex2].beginFrame + offset;
			
			// good for mel
//			int beginFrame1 = (datum1.tokenBoundaries[tokenIndex1].beginFrame + datum1.tokenBoundaries[tokenIndex1].endFrame)/2 + offset;
//			int beginFrame2 = (datum2.tokenBoundaries[tokenIndex2].beginFrame + datum2.tokenBoundaries[tokenIndex2].endFrame)/2 + offset;
			
			for (int frame=0; frame<width; ++frame) {
				int index1 = beginFrame1+frame;
				int index2 = beginFrame2+frame;
				if (index1 >= 0 && index2 >=0 && index1<datum1.response.length && index2<datum2.response.length) {
					
					// do resp
					float[] respFrame1 = a.toFloat(datum1.response[index1]);
					float[] respFrame2 = a.toFloat(datum2.response[index2]);
					
					// do mel
//					float[] respFrame1 = a.toFloat(datum1.mel[index1]);
//					float[] respFrame2 = a.toFloat(datum2.mel[index2]);
					
					// l2 dist
//					for (int j=0; j<respFrame1.length; ++j) {
//						float diff = respFrame1[j] - respFrame2[j];
//						score -= diff * diff;
//					}
					
					// cos dist
					float mag1 = 0.0f;
					for (int j=0; j<respFrame1.length; ++j) {
						mag1 += respFrame1[j] * respFrame1[j];
					}
					mag1 = (float) Math.sqrt(mag1);
					float mag2 = 0.0f;
					for (int j=0; j<respFrame2.length; ++j) {
						mag2 += respFrame2[j] * respFrame2[j];
					}
					mag2 = (float) Math.sqrt(mag2);
					float dotProd = 0.0f; 
					for (int j=0; j<respFrame1.length; ++j) {
						dotProd += respFrame1[j] * respFrame2[j];
					}
					score += dotProd / (mag1*mag2);
					
					
					count++;
				}
			}
			return (score / count);
		}
	}
	
	int k;
	List<LabeledDatum> data;
	SimilarityMetric simMetric;

	private KNNModel(List<LabeledDatum> data, SimilarityMetric simMetric, int k) {
		this.data = data;
		this.k = k;
		this.simMetric = simMetric;
	}
	
	public static Model train(List<LabeledDatum> data, SimilarityMetric simMetric, int k) {
		return new KNNModel(data, simMetric, k);
	}
	
	public LabeledDatum predict(Datum datum) {
		final Token[] predictedPhones = new Token[datum.tokenBoundaries.length];
		BetterThreader.Function<Integer,Object> func = new BetterThreader.Function<Integer,Object>(){public void call(Integer tokenIndex, Object ignore){
			predictedPhones[tokenIndex] = predict(datum, tokenIndex);
		}};
		BetterThreader<Integer,Object> threader = new BetterThreader<Integer,Object>(func, 8);
		for (int tokenIndex=0; tokenIndex<predictedPhones.length; ++tokenIndex) threader.addFunctionArgument(tokenIndex);
		threader.run();
		return new LabeledDatum(datum, predictedPhones, null, null);
	}
	
	private Token predict(Datum datum, int tokenIndex) {
		PriorityQueue<Pair<Integer,Integer>> queue = new PriorityQueue<Pair<Integer,Integer>>();
		for (int labDatumIndex=0; labDatumIndex<data.size(); ++labDatumIndex) {
			LabeledDatum labDatum = data.get(labDatumIndex);
			for (int labTokenIndex=0; labTokenIndex<labDatum.tokenBoundaries.length; ++labTokenIndex) {
				double sim = simMetric.similarity(datum, tokenIndex, labDatum, labTokenIndex);
				queue.put(Pair.makePair(labDatumIndex, labTokenIndex), -sim);
				while(queue.size() > k) {
					queue.next();
				}
			}
		}
		Counter<String> election = new Counter<String>();
		while (!queue.isEmpty()) {
			Pair<Integer,Integer> voter = queue.next();
			String label = data.get(voter.getFirst()).labels[voter.getSecond()].label;
			election.incrementCount(label, 1.0);
		}
		return new Token(election.argMax(), datum.tokenBoundaries[tokenIndex].beginFrame, datum.tokenBoundaries[tokenIndex].endFrame);
	}

}
