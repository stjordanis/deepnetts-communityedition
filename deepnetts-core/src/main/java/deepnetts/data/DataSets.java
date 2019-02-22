package deepnetts.data;

import java.io.File;
import java.io.IOException;
import deepnetts.util.DeepNettsException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


/**
 *
 * @author zoran
 */
public class DataSets {
   /**
     * Creates and returns data set from specified CSV file. Empty lines are
     * skipped
     *
     * @param csvFile CSV file
     * @param inputsNum number of input values in a row
     * @param outputsNum number of output values in a row
     * @param hasColumnNames true if first row contains column names
     * @param delimiter delimiter used to separate values
     * @return instance of data set with values loaded from file
     *
     * @throws FileNotFoundException if file was not found
     * @throws IOException if there was an error reading file
     *
     * TODO: Detect if there are labels in the first line, if there are no
     * labels, set class1, class2, class3 in classifier evaluation! and detect
     * type of attributes Move this method to some factory class or something?
     * or as a default method in data set?
     *
     *  TODO: should I wrap IO with DeepNetts Exception?
     */
    public static BasicDataSet readCsv(File csvFile, int inputsNum, int outputsNum, boolean hasColumnNames, String delimiter) throws FileNotFoundException, IOException {
        BasicDataSet dataSet = new BasicDataSet(inputsNum, outputsNum);
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        String line=null;
        // auto detect column names - ako sadrzi slova onda ima imena. Sta ako su atributi nominalni? U ovoj fazi se pretpostavlja d anisu...
        // i ako u redovima ispod takodje ima stringova u istoj koloni - detect header
        if (hasColumnNames) {    // get col names from the first line
            line = br.readLine();
            String[] colNames = line.split(delimiter);
            // todo checsk number of col names
            dataSet.setColumnNames(colNames);
        } else {
            String[] colNames = new String[inputsNum+outputsNum];
            for(int i=0; i<inputsNum;i++)
                colNames[i] = "in"+(i+1);

            for(int j=0; j<outputsNum;j++)
                colNames[inputsNum+j] = "out"+(j+1);

            dataSet.setColumnNames(colNames);
        }

        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) {
                continue; // skip empty lines
            }
            String[] values = line.split(delimiter);
            if (values.length != (inputsNum + outputsNum)) {
                throw new DeepNettsException("Wrong number of values in the row " + (dataSet.size() + 1) + ": found " + values.length + " expected " + (inputsNum + outputsNum));
            }
            float[] in = new float[inputsNum];
            float[] out = new float[outputsNum];

            try {
                // these methods could be extracted into parse float vectors
                for (int i = 0; i < inputsNum; i++) { //parse inputs
                    in[i] = Float.parseFloat(values[i]);
                }

                for (int j = 0; j < outputsNum; j++) { // parse outputs
                    out[j] = Float.parseFloat(values[inputsNum + j]);
                }
            } catch (NumberFormatException nex) {
                throw new DeepNettsException("Error parsing csv, number expected line in " + (dataSet.size() + 1) + ": " + nex.getMessage(), nex);
            }

            dataSet.add(new BasicDataSetItem(in, out));
        }

        return dataSet;
    }

    public static BasicDataSet readCsv(String fileName, int inputsNum, int outputsNum, boolean hasColumnNames, String delimiter) throws IOException {
         return readCsv(new File(fileName), inputsNum, outputsNum, hasColumnNames, delimiter);
    }

    public static BasicDataSet readCsv(String fileName, int inputsNum, int outputsNum, boolean hasColumnNames) throws IOException {
        return readCsv(new File(fileName), inputsNum, outputsNum, hasColumnNames, ",");
    }

    public static BasicDataSet readCsv(String fileName, int inputsNum, int outputsNum, String delimiter) throws IOException {
        return readCsv(new File(fileName), inputsNum, outputsNum, false, delimiter);
    }

    /**
     * Create data set from CSV file, using coma (,) as default delimiter and no
     * header (column names) in first row.
     *
     * @param fileName  Name of the CSV file
     * @param inputsNum Number of input columns
     * @param outputsNum Number of output columns
     * @return
     * @throws IOException
     */
    public static BasicDataSet readCsv(String fileName, int inputsNum, int outputsNum) throws IOException {
        return readCsv(new File(fileName), inputsNum, outputsNum, false, ",");
    }

    public static DataSet normalizeMax(DataSet dataSet, boolean inplace) {
        // instantiate MaxNormalizer
        return null;
    }

    // encode single row
    public static float[] oneHotEncode(final String label, final String[] labels) {   // different labels
        final float[] vect = new float[labels.length];
        // ako su brojeci i ako su stringovi, ako su sve nule, negative ...

        for(int i=0; i<labels.length; i++) {
            if (labels[i].equals(label)) {
                vect[i] = 1;
            }
        }
        // kako rsiti negative vektore?
        return vect;
    }

    public static DataSet[] trainTestSplit(DataSet dataSet, double split) {
        dataSet.shuffle();
        return dataSet.split(split, 1-split);
    }


//    public static float[] oneHotEncode(final int i, final int categories) {
//
//    }

//    public static DataSet random(int inputsNum, int outputsNum, inst size) {
//
//
//    }


}
