package ecog.data;

import ecog.main.EcogExperiment;

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
    public static final int N_MEL_FILTERS = 80;
    public static final int AUDIO_SAMPLE_RATE_HZ = 16000;
    public static final int ECOG_SAMPLE_RATE_HZ = 100;
	public static final int MCEP_DIM = 24;

    public final List<LabeledDatum> train;
    public final List<LabeledDatum> dev;
    public final List<LabeledDatum> test;

    public Dataset(List<LabeledDatum> train, List<LabeledDatum> dev, List<LabeledDatum> test) {
        this.train = train;
        this.dev = dev;
        this.test = test;
    }

    public static Dataset load(int startTrain, int numTrain, int startDev, int numDev, int startTest, int numTest) {
        List<LabeledDatum> data = new ArrayList<LabeledDatum>();

        for (File csvFile : new File(makeResponsePath()).listFiles()) {
            String timitName = csvFile.getName().substring(0, csvFile.getName().length() - 4);
            File melFile = new File(makeMelPath() + "/" + timitName + ".csv");
            try {
                double[][] response = loadCSVDoubleMatrix(csvFile, N_ELECTRODES);
                double[][] mel = loadCSVDoubleMatrix(melFile, N_MEL_FILTERS);
                Token[] phoneData = loadTimitSegmentation(new File(makeTimitPath(timitName, "phn")));
                Token[] wordData = loadTimitSegmentation(new File(makeTimitPath(timitName, "wrd")));
                double[][] mcep = BinaryIO.readDoubleMatrix(makeTimitPath(timitName+"_vc", "mcep24"), MCEP_DIM);
                if (phoneData.length == 0) {
                    continue;
                }
                data.add(new LabeledDatum(new Datum(response, mel, phoneData), phoneData, wordData, mcep));
            } catch (IOException e) {
                System.err.println("Exception encountered when loading data:");
                e.printStackTrace();
            }
        }

        List<LabeledDatum> train = data.subList(startTrain, startTrain+numTrain);
        List<LabeledDatum> dev = data.subList(startDev, startDev+numDev);
        List<LabeledDatum> test = data.subList(startTest, startTest+numTest);

        System.out.println("Train:");
        printDatasetInfo(train);
        System.out.println("Dev:");
        printDatasetInfo(dev);
        System.out.println("Test:");
        printDatasetInfo(test);

        return new Dataset(train, dev, test);
    }

    private static void printDatasetInfo(List<LabeledDatum> set) {
    	int numResponseFrames = 0;
    	int numMelFrames = 0;
    	int numPhones = 0;
    	int numWords = 0;
    	for (LabeledDatum datum : set) {
    		numResponseFrames += datum.response.length;
    		numMelFrames += datum.mel.length;
    		numPhones += datum.phoneLabels.length;
    		numWords += datum.wordLabels.length;
    	}
    	System.out.println("Num sentences: "+set.size());
    	System.out.println("Avg response frames / sent: "+numResponseFrames/((double) set.size()));
    	System.out.println("Avg mel frames / sent: "+numMelFrames/((double) set.size()));
    	System.out.println("Avg phones / sent: "+numPhones/((double) set.size()));
    	System.out.println("Avg words / sent: "+numWords/((double) set.size()));
    }

    private static String makeResponsePath() {
        return EcogExperiment.dataRoot + "/" + EcogExperiment.patient + "/csv";
    }

    private static String makeMelPath() {
        return EcogExperiment.dataRoot + "/" + EcogExperiment.patient + "/mel";
    }

    private static String makeTimitPath(String timitName, String extension) {
        return EcogExperiment.dataRoot + "/sounds/" + timitName + "." + extension;
    }

    private static double[][] loadCSVDoubleMatrix(File csvFile, int nItems) throws IOException {
        double[][] response = null;
        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
        String l;
        int electrode = 0;
        while ((l = reader.readLine()) != null) {
            String[] parts = l.split(",");
            if (response == null) {
                response = new double[parts.length][nItems];
            }
            for (int frame = 0; frame < parts.length; frame++) {
                response[frame][electrode] = Double.parseDouble(parts[frame]);
            }
            electrode++;
        }
        assert electrode == nItems;
        reader.close();
        return response;
    }
    
    private static Token[] loadTimitSegmentation(File timitFile) throws IOException {
        ArrayList<Token> tokens = new ArrayList<Token>();
        BufferedReader reader = new BufferedReader(new FileReader(timitFile));
        String l;
        while ((l = reader.readLine()) != null) {
            String[] parts = l.split(" ");
            int start = (int)(Double.parseDouble(parts[0]) / AUDIO_SAMPLE_RATE_HZ * ECOG_SAMPLE_RATE_HZ);
            int end = (int)(Double.parseDouble(parts[1]) / AUDIO_SAMPLE_RATE_HZ * ECOG_SAMPLE_RATE_HZ);
            tokens.add(new Token(parts[2], start, end));
        }
        reader.close();
        return tokens.toArray(new Token[tokens.size()]);
    }

}
