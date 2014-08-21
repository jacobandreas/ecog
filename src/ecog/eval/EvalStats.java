package ecog.eval;

/**
 * @author jda
 */
public class EvalStats {

    //private final double tp, fp, fn;
    //private final double prec, rec, f1;
    private final double per;

    public EvalStats(double predictions, double correct) {
//        this.tp = tp;
//        this.fp = fp;
//        this.fn = fn;
//        prec = tp / (tp + fp);
//        rec = tp / (tp + fn);
//        f1 = 2 * prec * rec / (prec + rec);
        per = correct / predictions;
    }

    public String toString() {
        return String.format("Phone error rate: %f", per);
    }
}
