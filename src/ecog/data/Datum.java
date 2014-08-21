package ecog.data;

/**
 * @author jda
 */
public class Datum {

    public final double[][] response;
    public final double[][] mel;
    public final Token[] tokenBoundaries;

    public Datum(double[][] response, double[][] mel, Token[] tokenBoundaries) {
        this.response = response;
        this.mel = mel;
        this.tokenBoundaries = tokenBoundaries;
    }

    public double[] computeAverageForTimestep(int t) {
        double[] r = new double[Dataset.N_ELECTRODES];
        int frame = tokenBoundaries[t].beginFrame;
        for (int f = frame + 15; f < frame + 20; f++) {
            if (f >= response.length) continue;
            for (int e = 0; e < Dataset.N_ELECTRODES; e++) {
                r[e] += response[f][e];
            }
        }
        return r;
    }
}
