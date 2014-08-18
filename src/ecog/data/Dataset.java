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

    public static final int N_ELECTRODES = 256;
    public static final int AUDIO_SAMPLE_RATE_HZ = 16000;
    public static final int ECOG_SAMPLE_RATE_HZ = 100;

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

        for (File csvFile : new File(makeEcogPath()).listFiles()) {
            String timitName = csvFile.getName().substring(0, csvFile.getName().length() - 4);
            try {
                double[][] response = loadResponse(csvFile);
                Token[] phoneData = loadTimit(new File(makeTimitPath(timitName, "phn")));
                Token[] wordData = loadTimit(new File(makeTimitPath(timitName, "wrd")));
                if (phoneData.length == 0) {
                    continue;
                }
                data.add(new LabeledDatum(new Datum(response, phoneData), phoneData, wordData));
            } catch (IOException e) {
                System.err.println("Exception encountered when loading data:");
                e.printStackTrace();
            }
        }

        data = data.subList(0, Math.min(data.size(), EcogExperiment.nRecordings));

        Pair<Integer, Integer> split = makeSplit(data.size());
        //Pair<Integer, Integer> split = new Pair<Integer,Integer>(1, 2);

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

    private static double[][] loadResponse(File csvFile) throws IOException {
        double[][] response = null;
        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
        String l;
        int electrode = 0;
        while ((l = reader.readLine()) != null) {
            String[] parts = l.split(",");
            if (response == null) {
                response = new double[parts.length][N_ELECTRODES];
            }
            for (int frame = 0; frame < parts.length; frame++) {
                response[frame][electrode] = Double.parseDouble(parts[frame]);
            }
            electrode++;
        }
        assert electrode == N_ELECTRODES;
        return response;
    }

    private static Token[] loadTimit(File timitFile) throws IOException {
        ArrayList<Token> tokens = new ArrayList<Token>();
        BufferedReader reader = new BufferedReader(new FileReader(timitFile));
        String l;
        while ((l = reader.readLine()) != null) {
            String[] parts = l.split(" ");
            int start = (int)(Double.parseDouble(parts[0]) / AUDIO_SAMPLE_RATE_HZ * ECOG_SAMPLE_RATE_HZ);
            int end = (int)(Double.parseDouble(parts[1]) / AUDIO_SAMPLE_RATE_HZ * ECOG_SAMPLE_RATE_HZ);
            tokens.add(new Token(parts[2], start, end));
        }
        return tokens.toArray(new Token[tokens.size()]);
    }

    private static Pair<Integer,Integer> makeSplit(int totalCount) {
        return new Pair<Integer,Integer>(totalCount / 2, totalCount * 3 / 4);
    }

}
