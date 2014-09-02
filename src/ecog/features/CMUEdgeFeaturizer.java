package ecog.features;

import arrays.a;
import counter.Counter;
import ecog.data.Datum;
import indexer.Indexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author jda
 */
public class CMUEdgeFeaturizer implements EdgeFeaturizer {

    private double[][] transitionScores;

    public CMUEdgeFeaturizer(String trainPath, String symbolMapPath, Indexer<String> phoneIndex) {
        try {
            Map<String, Set<String>> symbolMap = loadSymbolMap(symbolMapPath);
            transitionScores = loadTransitionScores(trainPath, symbolMap, phoneIndex);
        } catch (IOException e) {
            System.err.println("Error loading transition model from " + trainPath);
            e.printStackTrace();
        }
    }

    private static Map<String,Set<String>> loadSymbolMap(String symbolMapPath) throws IOException {
        Map<String,Set<String>> map = new HashMap<String,Set<String>>();
        BufferedReader reader = new BufferedReader(new FileReader(symbolMapPath));
        String l;
        while ((l = reader.readLine()) != null) {
            String[] parts = l.split(" ");
            //map.put(parts[0], parts[1]);
            map.put(parts[0], new HashSet<String>(Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length))));
        }
        return map;
    }

    private static double[][] loadTransitionScores(String trainPath, Map<String,Set<String>> symbolMap, Indexer<String> phoneIndex) throws IOException {
        double[][] transitionScores = new double[phoneIndex.size()][phoneIndex.size()];
        BufferedReader reader = new BufferedReader(new FileReader(trainPath));
        String l;
        while ((l = reader.readLine()) != null) {
            String[] toks = ("# " + l + " #").split("\\s+");
            for (int i = 0; i < toks.length - 1; i++) {
//                System.out.println(toks[i]);
                for (String p1 : symbolMap.get(toks[i])) {
//                    System.out.println(Arrays.toString(toks));
//                    System.out.println(toks[i+1]);
                    for (String p2 : symbolMap.get(toks[i+1])) {
                        if (!phoneIndex.contains(p1) || !phoneIndex.contains(p2)) continue;
                        int s1 = phoneIndex.getIndex(p1);
                        int s2 = phoneIndex.getIndex(p2);
                        transitionScores[s1][s2] += 1;
                    }
                }
            }
        }
        a.addi(transitionScores, 1);
        a.normalizecoli(transitionScores);
        a.logi(transitionScores);
        return transitionScores;
    }

    @Override
    public Counter<String> apply(Datum datum, int state1, int state2) {
        Counter<String> r = new Counter<String>();
        r.setCount("TRANS_CMU_LM", transitionScores[state1][state2]);
//        r.setCount("TRANS_" + state1 + "_" + state2, 1);
        return r;
    }
}
