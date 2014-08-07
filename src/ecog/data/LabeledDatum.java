package ecog.data;

/**
 * @author jda
 */
public class LabeledDatum extends Datum {

    public final Datum wrapped;
    public final Token[] phoneLabels;
    public final Token[] wordLabels;
    public final Token[] labels;

    public LabeledDatum(Datum wrapped, Token[] phoneLabels, Token[] wordLabels) {
        super(wrapped.response, phoneLabels);
        this.wrapped = wrapped;
        this.phoneLabels = phoneLabels;
        this.wordLabels = wordLabels;
        this.labels = phoneLabels;
    }
}
