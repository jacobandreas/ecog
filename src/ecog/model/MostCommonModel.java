package ecog.model;

import java.util.List;

import counter.Counter;
import ecog.data.Datum;
import ecog.data.LabeledDatum;
import ecog.data.Token;

public class MostCommonModel extends Model {
	
	String mostCommonLabel;

	private MostCommonModel(List<LabeledDatum> data) {
		Counter<String> election = new Counter<String>();
		for (int labDatumIndex=0; labDatumIndex<data.size(); ++labDatumIndex) {
			LabeledDatum labDatum = data.get(labDatumIndex);
			for (int labTokenIndex=0; labTokenIndex<labDatum.labels.length; ++labTokenIndex) {
				String label = labDatum.labels[labTokenIndex].label;
				election.incrementCount(label, 1.0);
			}
		}
		mostCommonLabel = election.argMax();
	}
	
	public static Model train(List<LabeledDatum> data) {
		return new MostCommonModel(data);
	}
	
	public LabeledDatum predict(Datum datum) {
		Token[] predictedPhones = new Token[datum.tokenBoundaries.length];
		for (int tokenIndex=0; tokenIndex<predictedPhones.length; ++tokenIndex) {
			predictedPhones[tokenIndex] = new Token(mostCommonLabel, datum.tokenBoundaries[tokenIndex].beginFrame, datum.tokenBoundaries[tokenIndex].endFrame);
		}
		return new LabeledDatum(datum, predictedPhones, null);
	}
	
}
