package ecog.main;

import ecog.data.Dataset;
import ecog.eval.EvalStats;
import ecog.model.CRFModel;
import ecog.model.Model;
import fig.Execution;
import fig.Option;

/**
 * @author jda
 */
public class EcogExperiment implements Runnable {

    @Option(gloss = "path to ECOG data")
    public static String dataRoot;

    @Option(gloss = "path to patient (e.g. 'left_grid/EC36')")
    public static String patient = "left_grid/EC36";

    public void run() {
        Dataset data = Dataset.load();
        Model model = CRFModel.train(data.train);
        EvalStats eval = model.evaluate(data.dev);
    }

    public static void main(String[] args) {
        EcogExperiment experiment = new EcogExperiment();
        Execution.run(args, experiment);
    }
}
