package ecog.eval;

/**
 * @author jda
 */
public class EvalStats {

    private final double tp, fp, fn;
    private final double prec, rec, f1;

    public EvalStats(double tp, double fp, double fn) {
        this.tp = tp;
        this.fp = fp;
        this.fn = fn;
        prec = tp / (tp + fp);
        rec = tp / (tp + fn);
        f1 = 2 * prec * rec / (prec + rec);
    }

    public String toString() {
        return String.format("P: %f, R: %f, F1: %f", prec, rec, f1);
    }
}
