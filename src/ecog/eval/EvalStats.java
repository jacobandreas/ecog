package ecog.eval;

/**
 * @author jda
 */
public class EvalStats {

    private final double acc;

    public EvalStats(double predictions, double correct) {
        acc = correct / predictions;
    }

    public String toString() {
        return String.format("Phone accuracy: %f", acc);
    }
}
