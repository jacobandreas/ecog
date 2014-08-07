package ecog.data;

/**
 * @author jda
 */
public class Datum {

    public final double[][] response;
    public final Token[] tokenBoundaries;

    public Datum(double[][] response, Token[] tokenBoundaries) {
        this.response = response;
        this.tokenBoundaries = tokenBoundaries;
    }
}
