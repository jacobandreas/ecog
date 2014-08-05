package ecog.data;

/**
 * @author jda
 */
public class LabeledDatum extends Datum {

    public final Datum wrapped;
    public final Token[] phoneLabels;
    public final Token[] wordLabels;

    public LabeledDatum(Datum wrapped, Token[] phoneLabels, Token[] wordLabels) {
        super(wrapped.response);
        this.wrapped = wrapped;
        this.phoneLabels = phoneLabels;
        this.wordLabels = wordLabels;
    }
}
