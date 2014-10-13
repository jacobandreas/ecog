package ecog.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BinaryIO {

	public static double[][] readDoubleMatrix(String path, int numCols) {
		List<double[]> matrixList  = new ArrayList<double[]>();
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(path));
			double[] row = null;
			int colNum = 0;
			boolean EOF = false;
			while (!EOF) {
			    try {
			    	if (colNum == 0) {
			    		if (row != null) matrixList.add(row);
			    		row = new double[numCols];
			    	}
			    	row[colNum] = Double.longBitsToDouble(Long.reverseBytes(in.readLong()));
			    	colNum = (colNum + 1) % numCols;
			    } catch (EOFException e) {
			        EOF = true;
			    }
			}  
		} catch (Exception e) {
			e.printStackTrace();
		}
		double[][] matrix = new double[matrixList.size()][numCols];
		for (int r=0; r<matrix.length; ++r) {
			matrix[r] = matrixList.get(r);
		}
		return matrix;
	}
	
	public static void writeDoubleMatrix(String path, double[][] matrix) {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(path));
			for (int r=0; r<matrix.length; ++r) {
				for (int c=0; c<matrix[r].length; ++c) {
					out.writeLong(Long.reverseBytes(Double.doubleToLongBits(matrix[r][c])));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] arg) {
		double[][] originalMatrix = readDoubleMatrix("/home/tberg/festival_package/festvox/src/vc/mcep/awb/arctic_a0070.mcep", 24);
		writeDoubleMatrix("/home/tberg/festival_package/festvox/src/vc/test/wav/awb-slt/arctic_a0070.wav.conv.mcep24", originalMatrix);
	}

}
