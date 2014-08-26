package ecog.model;

import java.util.List;

import ecog.data.Datum;
import ecog.data.LabeledDatum;

public class KNNModel extends Model {

	private KNNModel(List<LabeledDatum> data) {
		
	}
	
	public static Model train(List<LabeledDatum> data) {
		return new KNNModel(data);
	}
	
	public LabeledDatum predict(Datum datum) {
		return null;
	}

}
