package ecog.preproc;

import ecog.data.Dataset;
import ecog.data.Datum;
import ecog.data.LabeledDatum;
import arrays.a;

import org.jblas.FloatMatrix;
import org.jblas.MatrixFunctions;
import org.jblas.Solve;
import org.jblas.Eigen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jda
 */
public class CCAPreproc {

    public static final int N_RESP_COMPS = Dataset.N_ELECTRODES / 2;
    public static final int N_MEL_COMPS = Dataset.N_MEL_FILTERS / 2;

    public static Dataset doCCA(Dataset data) {
        int nObservations = 0;
        for (LabeledDatum datum : data.train) {
            nObservations += datum.response.length;
        }

        float[][] responseData = new float[nObservations][Dataset.N_ELECTRODES];
        float[][] melData = new float[nObservations][Dataset.N_MEL_FILTERS];
        int i = 0;
        for (LabeledDatum datum : data.train) {
            assert(datum.response.length == datum.mel.length);
            for (int j = 0; j < datum.response.length; j++) {
                responseData[i] = a.toFloat(datum.response[j]);
                melData[i] = a.toFloat(datum.mel[j]);
            }
        }

        FloatMatrix responseMatrix = new FloatMatrix(responseData);
        FloatMatrix melMatrix = new FloatMatrix(melData);

        normalizei(responseMatrix);
        normalizei(melMatrix);

        // center & scale
        //normalizei(responseMatrix);

//        System.out.println(melMatrix.getRow(0));
//        System.out.println(melMatrix.getRow(100));
//        normalizei(melMatrix);
//        normalizei(responseMatrix);
//        System.out.println();
//        System.out.println(melMatrix.getRow(0));
//        System.out.println(melMatrix.getRow(100));


        // build cov matrices
        FloatMatrix sRR = responseMatrix.transpose().mmul(responseMatrix);
        FloatMatrix sMM = melMatrix.transpose().mmul(melMatrix);
        FloatMatrix sRM = responseMatrix.transpose().mmul(melMatrix);
        FloatMatrix sMR = sRM.transpose();

        sRR.addi(FloatMatrix.eye(sRR.rows));
        sMM.addi(FloatMatrix.eye(sMM.rows));

        // sRR \ sRM / sMM * sMR
        FloatMatrix rMat = Solve.solveSymmetric(sMM, Solve.solveSymmetric(sRR, sRM).transpose()).transpose().mmul(sMR);
        FloatMatrix mMat = Solve.solveSymmetric(sRR, Solve.solveSymmetric(sMM, sMR).transpose()).transpose().mmul(sRM);
        //FloatMatrix rMat = Solve.pinv(sRR).mmul(sRM).mmul(Solve.pinv(sMM)).mmul(sMR);

        FloatMatrix rEigs = Eigen.eigenvectors(rMat)[0].real().getColumns(a.enumerate(0, N_RESP_COMPS));
        FloatMatrix mEigs = Eigen.eigenvectors(mMat)[0].real().getColumns(a.enumerate(0, N_MEL_COMPS));

        @SuppressWarnings("unchecked")
        List<LabeledDatum>[] splits = new List[] { data.train, data.dev, data.test };
        @SuppressWarnings("unchecked")
        ArrayList<LabeledDatum>[] outs = new ArrayList[3];

        for (int d = 0; d < splits.length; d++) {
            outs[d] = new ArrayList<LabeledDatum>();
            i = 0;
            for (LabeledDatum datum : splits[d]) {
                FloatMatrix datumResponse = new FloatMatrix(a.toFloat(datum.response));
                FloatMatrix datumMel = new FloatMatrix(a.toFloat(datum.mel));
                FloatMatrix projectedResponse = datumResponse.mmul(rEigs);
                FloatMatrix projectedMel = datumMel.mmul(mEigs);

                Datum baseDatum = new Datum(a.toDouble(projectedResponse.toArray2()),
                        a.toDouble(projectedMel.toArray2()),
                        datum.tokenBoundaries);
                LabeledDatum outDatum = new LabeledDatum(baseDatum, datum.phoneLabels, datum.wordLabels, null);
                outs[d].add(outDatum);
            }
        }

        return new Dataset(outs[0], outs[1], outs[2]);
    }

    static void normalizei(FloatMatrix m) {
        FloatMatrix mean = m.columnSums().divi(m.rows);
        m.subiRowVector(mean);

        // data is already centered?

        FloatMatrix sq = m.mul(m);
        FloatMatrix var = sq.columnSums();
        var.divi(m.rows);
        MatrixFunctions.sqrti(var);
        m.diviRowVector(var);

        // use 1st 15 comps

    }
}
