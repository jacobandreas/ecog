package ecog.data;

import ecog.main.EcogExperiment;
import tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jda
 */
public class Dataset {

    public final List<LabeledDatum> train;
    public final List<LabeledDatum> dev;
    public final List<LabeledDatum> test;

    public Dataset(List<LabeledDatum> train, List<LabeledDatum> dev, List<LabeledDatum> test) {
        this.train = train;
        this.dev = dev;
        this.test = test;
    }

    public static Dataset load() {

        List<LabeledDatum> data = new ArrayList<LabeledDatum>();

        System.out.println(makeEcogPath());
        for (File csvFile : new File(makeEcogPath()).listFiles()) {
            String timitName = csvFile.getName().substring(0, csvFile.getName().length() - 4);
            try {
                double[][] response = loadResponse(csvFile);
                Token[] phoneData = loadTimit(new File(makeTimitPath(timitName, "phn")));
                Token[] wordData = loadTimit(new File(makeTimitPath(timitName, "wrd")));
                data.add(new LabeledDatum(new Datum(response), phoneData, wordData));
            } catch (IOException e) {
                System.err.println("Exception encountered when loading data:");
                e.printStackTrace();
            }
        }

        Pair<Integer, Integer> split = makeSplit(data.size());

        List<LabeledDatum> train = data.subList(0, split.getFirst());
        List<LabeledDatum> dev = data.subList(split.getFirst(), split.getSecond());
        List<LabeledDatum> test = data.subList(split.getSecond(), data.size());

        return new Dataset(train, dev, test);

    }

    private static String makeEcogPath() {
        return EcogExperiment.dataRoot + "/" + EcogExperiment.patient + "/csv";
    }

    private static String makeTimitPath(String timitName, String extension) {
        return EcogExperiment.dataRoot + "/sounds/" + timitName + "." + extension;
    }

    private static double[][] loadResponse(File csvFile) {
        return null;
    }

    private static Token[] loadTimit(File timitFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(timitFile));
        ArrayList<Token> tokens = new ArrayList<Token>();
        String l;
        while ((l = reader.readLine()) != null) {
            String[] parts = l.split(" ");
            tokens.add(new Token(parts[2], Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
        }
        return tokens.toArray(new Token[tokens.size()]);
    }

    private static Pair<Integer,Integer> makeSplit(int totalCount) {
        return new Pair<Integer,Integer>(totalCount / 3, totalCount * 2 / 3);
    }

}
