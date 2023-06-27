package kosh.formaters;

import kosh.Data;
import kosh.util.StringUtils;

import java.awt.Color;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

public abstract class AFileFormater {
    private File[] fileList;
    private int chosenOne = 0;
    private String[][] bandsDescriptions;

    protected float linTransformParam = 0.02f;
    private final int histAccuracy = 256 * 2;


    /**
     * @return number of spectral bands in all files or 0
     */
    public int getBandsNumber_M() {
        if (bandsDescriptions == null) {
            return 0;
        }

        int n = 0;
        for (String[] bandsDescription : bandsDescriptions)
            n += bandsDescription.length;
        return n;
    }

    /**
     * @return last opened file list or Null
     */
    public File[] getLastFiles_M() {
        return fileList.clone();
    }

    /**
     * @return list of bands descriptions from all files or NULL
     */
    public String[] getBandsDescription_M() {
        if (bandsDescriptions == null || fileList == null) {
            return null;
        }

        if (bandsDescriptions.length == 1) {
            return bandsDescriptions[0];
        }

        int n = getBandsNumber_M();
        String[] bds = new String[n];
        String[] fileMarks = getFileNameMarks(fileList);

        int j = 0;
        for (int i = 0; i < bandsDescriptions.length; i++) {
            String fileMark = fileMarks[i] + " ";
            String[] bdsBand = bandsDescriptions[i];
            for (String desc : bdsBand)
                bds[j++] = fileMark + desc;
        }

        return bds;
    }

    private String[] getFileNameMarks(File[] files) {
        // Not less than 2 elements in files[] array !
        final int n = files.length;

        // Get file names without extensions
        String[] fileNames = new String[n];
        for (int i = 0; i < n; i++) {
            String fileName = files[i].getName();
            fileNames[i] = StringUtils.cutExtension(fileName);
        }

        // Get filename parts that differ
        String[] nameDiff = new String[n];
        nameDiff[0] = StringUtils.diff(fileNames[0], fileNames[1]).first();
        for (int i = 1; i < n; i++) {
            nameDiff[i] = StringUtils.diff(fileNames[0], fileNames[i]).second();
        }

        // Form mark in form: "F1. difference ", or just "F1. " if difference is too long
        for (int i = 0; i < n; i++) {
            String str = "F" + (i + 1) + ". ";
            if (nameDiff[i].length() < 12) {
                str += (nameDiff[i] + " ");
            }
            fileNames[i] = str;
        }

        return fileNames;
    }

    public boolean loadHeader_M(File file) throws Exception {
        File[] files = {file};
        return loadHeader_M(files);
    }

    /**
     * Loading files info.
     *
     * @param files - file list
     * @return true if loading was successful and all images have same size; false otherwise
     * @throws Exception
     */
    public boolean loadHeader_M(File[] files) throws Exception {
        if (files == null || files.length == 0) {
            return false;
        }

        File[] flist = files.clone();
        final int num = flist.length;
        String[][] bandsDescs = new String[num][];

        if (! loadHeader(flist[0])) {
            return false;
        }
        final int w = getWidth();
        final int h = getHeight();
        double res = getResolution();
        bandsDescs[0] = getBandsDescription();
        int chosen = 0;

        for (int i = 1; i < num; i++) {
            if (! loadHeader(flist[i])) {
                return false;
            }
            if (w != getWidth() || h != getHeight()) {
                throw new Exception("width or height of 1st file != width or height of " +
                                    "" + i + " , w0: " + w + ", h0: " + h + ", curW: " + getWidth() + ", curH: " + getHeight());
            }
            double res_i = getResolution();
            if (res_i != 0) {
                if (res == 0) {
                    res = res_i;
                    chosen = i;
                } else {
                    if (res != res_i) {
                        throw new Exception("resolution != resI");
                    }
                }
            }
            bandsDescs[i] = getBandsDescription();
        }

        fileList = flist;
        chosenOne = chosen;
        bandsDescriptions = bandsDescs;
        if (num > 1) {
            reset();
        }

        return true;
    }

    private void reset() {
        if (fileList != null) {
            loadHeader(fileList[chosenOne]);
        }
    }

    /**
     * Loading bands from files directly as float
     * If activeBands is null, all bands are loaded
     *
     * @param _activeBands - list of bands to load, for all files together as given by getBandsDescription_M()
     * @return selected bands or null if something is wrong
     */
    public float[][] loadData_float_M(boolean[] _activeBands) {
        boolean[] activeBands = (_activeBands == null) ? null : _activeBands.clone();

        final int numF = fileList.length;
        boolean[][] activeBandsF = transformActiveBandsAndCheck(activeBands);
        if (activeBandsF == null) {
            return null;
        }

        if (numF > 1) {
            // Load
            int numActiveBands = 0;
            float[][][] dataF = new float[numF][][];
            for (int i = 0; i < numF; i++) {
                // Check if no bands were selected for this file
                boolean[] activeBandsFI = activeBandsF[i];
                if (IntStream.range(0, activeBandsFI.length).noneMatch(j -> activeBandsFI[j])) {
                    continue; // dataF[i]=null;
                }

                loadHeader(fileList[i]);
                dataF[i] = loadData_float(activeBandsF[i]);
                if (dataF[i] == null) {
                    return null;
                }
                numActiveBands += dataF[i].length;
            }

            // Form result as one array
            float[][] data = new float[numActiveBands][];
            int j = 0;
            for (int i = 0; i < numF; i++) {
                if (dataF[i] == null) {
                    continue;
                }
                for (int k = 0; k < dataF[i].length; k++)
                    data[j++] = dataF[i][k];
            }

            reset();
            return data;
        } else {
            return loadData_float(activeBandsF[0]);
        }
    }

    private boolean[][] transformActiveBandsAndCheck(boolean[] activeBands) {
        if (bandsDescriptions == null || fileList == null) {
            return null;
        }

        final int numF = fileList.length;
        final int numBands = getBandsNumber_M();

        // Checks
        if (activeBands == null) {
            activeBands = new boolean[numBands];
            for (int i = 0; i < numBands; i++)
                activeBands[i] = true;
        }
        if (numBands != activeBands.length) {
            System.out.println("Invalid selected bands!");
            return null;
        }
        final boolean[] abArr = activeBands;
        if (IntStream.range(0, abArr.length).noneMatch(j -> abArr[j])) {
            System.out.println("No selected bands!");
            return null;
        }

        // Transform active bands for each file
        boolean[][] activeBandsF = new boolean[numF][];
        int j = 0;
        for (int i = 0; i < numF; i++) {
            int numBandsF = bandsDescriptions[i].length;
            activeBandsF[i] = Arrays.copyOfRange(activeBands, j, j + numBandsF);
            j += numBandsF;
        }

        return activeBandsF;
    }

    /**
     * Loading data-files into the new Data object.
     *
     * @param _activeBands - selected bands to load, or all bands if null
     * @return Data object filled with data from file; or null if loading was not successful
     */
    public Data loadData_M(boolean[] _activeBands) {
        boolean[] activeBands = (_activeBands == null) ? null : _activeBands.clone();

        final int numF = fileList.length;
        boolean[][] activeBandsF = transformActiveBandsAndCheck(activeBands);
        if (activeBandsF == null) {
            return null;
        }

        if (numF > 1) {
            // Load
            int numActiveBands = 0;
            Data[] data = new Data[numF];
            for (int i = 0; i < numF; i++) {
                // Check if no bands were selected for this file
                boolean[] activeBandsFI = activeBandsF[i];
                if (IntStream.range(0, activeBandsFI.length).noneMatch(j -> activeBandsFI[j])) {
                    continue; // data[i]=null;
                }

                loadHeader(fileList[i]);
                data[i] = loadData(activeBandsF[i]);
                if (data[i] == null) {
                    return null;
                }
                numActiveBands += data[i].getNumBands();
            }
            System.out.println("NUM ACTIVE BANDS: " + numActiveBands);

            // Form new band descriptions
            String[] bandsDescriptionAll = getBandsDescription_M();
            String[] activeBandsDescription = new String[numActiveBands];
            for (int i = 0, j = 0; i < activeBands.length; i++) {
                if (activeBands[i]) {
                    activeBandsDescription[j++] = bandsDescriptionAll[i];
                }
            }
            System.out.println("NEW BAND DESCR ALL: " + Arrays.toString(bandsDescriptionAll));
            System.out.println("NEW BAND DESCR ACTIVE: " + Arrays.toString(activeBandsDescription));

            // Form dataPoints as one
            short[][] dataPointsAll = new short[numActiveBands][];
            for (int i = 0, j = 0; i < numF; i++) {
                if (data[i] == null) {
                    continue;
                }
                short[][] dataPointsF = data[i].getDataPoints();
                for (short[] shorts : dataPointsF)
                    dataPointsAll[j++] = shorts;
            }

            // Form Data result as one
            Data dataAll = data[chosenOne];
            if (dataAll == null) {
                dataAll = Arrays.stream(data).filter(Objects::nonNull).findFirst().orElse(null);
            }
            dataAll.setActiveNum(numActiveBands);
//			dataAll.setDimData(numActiveBands, activeBandsDescription);
            dataAll.setDataPoints(dataPointsAll);


            reset();
            dataAll.setBandsDescriptions(activeBandsDescription);
            return dataAll;
        } else {
            return loadData(activeBandsF[0]);
        }
    }

    /**
     * Loading data from files into the new Data object.
     * Loading only elements, selected in the given mask.
     *
     * @param _activeBands - selected bands to load, or all bands if null
     * @param mask         - mask for loading some selected part of data
     * @param selectedInd  - mask value for selected elements to be loaded
     * @return Data object filled with selected data from file; or null if loading was not successful
     */
    public Data loadData_mask_M(boolean[] _activeBands, short[] mask, int selectedInd) {
        boolean[] activeBands = (_activeBands == null) ? null : _activeBands.clone();

        final int numF = fileList.length;
        boolean[][] activeBandsF = transformActiveBandsAndCheck(activeBands);
        if (activeBandsF == null) {
            return null;
        }

        if (numF > 1) {
            // Load
            int numActiveBands = 0;
            Data[] data = new Data[numF];
            for (int i = 0; i < numF; i++) {
                // Check if no bands were selected for this file
                boolean[] activeBandsFI = activeBandsF[i];
                if (IntStream.range(0, activeBandsFI.length).noneMatch(j -> activeBandsFI[j])) {
                    continue; // data[i]=null;
                }

                loadHeader(fileList[i]);
                data[i] = loadData_mask(activeBandsF[i], mask, selectedInd);
                if (data[i] == null) {
                    return null;
                }
                numActiveBands += data[i].getNumBands();
            }

            // Form new band descriptions
            String[] bandsDescriptionAll = getBandsDescription_M();
            String[] activeBandsDescription = new String[numActiveBands];
            int j = 0;
            for (int i = 0; i < activeBands.length; i++) {
                if (activeBands[i]) {
                    activeBandsDescription[j++] = bandsDescriptionAll[i];
                }
            }

            // Form dataPoints as one
            short[][] dataPointsAll = new short[numActiveBands][];
            for (int i = 0; i < numF; i++) {
                if (data[i] == null) {
                    continue;
                }
                short[][] dataPointsF = data[i].getDataPoints();
                for (short[] shorts : dataPointsF)
                    dataPointsAll[j++] = shorts;
            }

            // Form Data result as one
            Data dataAll = data[chosenOne];
            if (dataAll == null) {
                dataAll = Arrays.stream(data).filter(Objects::nonNull).findFirst().orElse(null);
            }
//			dataAll.setDimData(numActiveBands, activeBandsDescription);
            dataAll.setActiveNum(numActiveBands);
            dataAll.setDataPoints(dataPointsAll);

            reset();
            return dataAll;
        } else {
            return loadData_mask(activeBandsF[0], mask, selectedInd);
        }
    }


    /* RawData - ��������� ��� ������ ���������� ������ */
    public boolean setSaveRawDataMode_M(boolean mode) {
        if (fileList == null || fileList.length != 1) {
            return false;
        }

        setSaveRawDataMode(true);
        return true;
    }


    /* ������ �� ���������� ��� ������ � ����� ������ */

    /**
     * @return linear transform parameter value
     */
    public float getLinTransformParameter() {
        return linTransformParam;
    }

    /**
     * Changes linear transform parameter value if it is valid: from 0 to 1 exclusive.
     *
     * @param newValue - new linear transform parameter value
     * @return true if the parameter was successfully changed, false if the given value is invalid
     */
    public boolean setLinTransformParameter(float newValue) {
        if ((newValue < 0) || (newValue >= 1)) {
            return false;
        }
        linTransformParam = newValue;
        return true;
    }

    /**
     * @return loaded data width or default value = 0
     */
    public abstract int getWidth();

    /**
     * @return loaded data height or default value = 0
     */
    public abstract int getHeight();

    /**
     * @return number of spectral bands in the loaded data or default value = 1
     */
    protected abstract int getBandsNumber();

    /**
     * @return image resolution in meters or 0 if unknown
     */
    public abstract double getResolution();

    /**
     * @return last opened file or Null
     */
    public abstract File getLastFile();

    /**
     * Searches for the property with the specified key in the inner Properties object,
     * witch should be filled by loadHeader() method.
     * The method returns null if the property is not found.
     *
     * @param key - key value
     * @return the value in the current property list with the specified key value, or null if the property is not
     * found
     */
    public abstract String getProperty(String key);

    /**
     * Prints all the properties of the inner Properties object to the specified output stream.
     *
     * @param output - an output stream.
     */
    public abstract void printCurrentProperties(PrintStream output);

    /**
     * @return list of bands descriptions or just one element "1."
     */
    protected abstract String[] getBandsDescription();

    public abstract Color[] getColorTable();

    protected abstract void setSaveRawDataMode(boolean mode);

    public abstract boolean isRawDataAvaible(File file, int width, int height);

    public abstract float getRawDataPoint(int x, int y, int band);

    /**
     * Loading file info.
     *
     * @param file - file
     * @return true if loading was successful; false otherwise
     */
    protected abstract boolean loadHeader(File file);

    protected abstract float[][] loadData_float(boolean[] activeBands);

    /**
     * Loading data-file into the new Data object.
     *
     * @param activeBands - selected bands to load, or all bands if null
     * @return Data object filled with data from file; or null if loading was not successful
     */
    protected abstract Data loadData(boolean[] activeBands);

    /**
     * Loading data from file into the new Data object.
     * Loading only elements, selected in the given mask.
     *
     * @param activeBands - selected bands to load, or all bands if null
     * @param mask        - mask for loading some selected part of data
     * @param selectedInd - mask value for selected elements to be loaded
     * @return Data object filled with selected data from file; or null if loading was not successful
     */
    protected abstract Data loadData_mask(boolean[] activeBands, short[] mask, int selectedInd);

    /**
     * Saving classification result in file.
     *
     * @param file        - file to save classification
     * @param resultData  - Data object containing classification result
     * @param driverName  - file type, e.g.: "ENVI", "HFA" for (.img). Set to ENVI if null.
     * @param description - string that contains description of the applied classification method, or null
     * @param clColors    - colors for the classes, or null
     * @param clNames     - colors for the classes, or just null
     * @return true if the file was saved successfully; false otherwise
     */
    public abstract boolean saveClassification(File file, Data resultData, String driverName, String description,
                                               int[] clColors, String[] clNames);

    public abstract boolean saveClassification(File file, Data resultData, String driverName, String description,
                                               int[] clColors);

    /**
     * Saving raster data in its internal representation in file.
     *
     * @param file        - file to save data
     * @param resultData  - Data object containing raster to save
     * @param driverName  - file type, e.g.: "GTiff", "ENVI", "HFA" for (.img). Set to GTiff if null.
     * @param description - string that contains description of the data, or null
     * @param bandOrder   - save bands in the given order
     * @return true if the file was saved successfully; false otherwise
     */
    public abstract boolean saveRaster(File file, Data resultData, String driverName, String description,
                                       int[] bandOrder);

    public abstract boolean saveRaster(File file, Data resultData, String driverName, String description);


    // Linear transform methods

    /**
     * Converts limited data in floats to integer [0,255] range and saves it in the given Data object.
     * Conversion is linear with (linTransformParam) crop from each side.
     * Elements with zero values in all bands are not taken in transform process.
     * Float data and Data object should have same dimensions
     *
     * @param dataF - array of float values with data
     * @param dat   - Data object to be filled with data
     */
    public void convertTo256Data_PercentLinear_BlackMask(float[][] dataF, Data dat) {
        convertTo256Data_PercentLinear_BlackMask(dataF, dat, linTransformParam);
    }

    /**
     * Converts limited data in floats to integer [0,255] range and saves it in the given Data object.
     * Conversion is linear with linTransformThreshold crop from each side.
     * Elements with zero values in all bands are not taken in transform process.
     * Float data and Data object should have same dimensions
     *
     * @param dataF                 - array of float values with data
     * @param dat                   - Data object to be filled with data
     * @param linTransformThreshold - linear transform threshold parameter
     */
    public void convertTo256Data_PercentLinear_BlackMask(float[][] dataF, Data dat, final float linTransformThreshold) {
        // ��������� �����, ��������� �� ����� (����� ��������� ����-��)

        // dataF[bands*][n];
        final int n = dataF[0].length;    // = dat.getNData();
        final int bandNum = dataF.length;


        // Form max/min, (max-min) values for each band
        float[] min = new float[bandNum];
        for (int i = 0; i < min.length; i++)
            min[i] = dataF[i][0];
        float[] max = min.clone();
        for (int i = 0; i < bandNum; i++) {
            float minBand = min[i];
            float maxBand = max[i];
            float[] dataBandF = dataF[i];
            for (int x = 1; x < n; x++) {
                if (dataBandF[x] < minBand) {
                    minBand = dataBandF[x];
                }
                if (dataBandF[x] > maxBand) {
                    maxBand = dataBandF[x];
                }
            }
            min[i] = minBand;
            max[i] = maxBand;
        }
        // Find max/min of all bands
        float maxAllBands = max[0];
        for (float v : max)
            if (v > maxAllBands) {
                maxAllBands = v;
            }
        float minAllBands = min[0];
        for (float v : min)
            if (v < minAllBands) {
                minAllBands = v;
            }

        // Trying to avoid mask check
        if (maxAllBands <= 255) {
            convertTo256Data_PercentLinear(dataF, dat);
            return;
        }

        // Form max/min, (max-min) values for each band without 'masked' pixels
        min = new float[bandNum];
        for (int i = 0; i < bandNum; i++)
            min[i] = 150000000;
        max = new float[bandNum];
        for (int i = 0; i < bandNum; i++)
            max[i] = - 150000000;

        int validNumber = 0;
        for (int x = 0; x < n; x++) {
            // check !
            boolean valid = false;
            // bandNum-1, ���� ������ ���� NDVI � �����
            for (float[] floats : dataF) {
                if (floats[x] != 0) {
                    valid = true;
                    break;
                }
            }
            if (valid) {
                validNumber++;
                for (int i = 0; i < bandNum; i++) {
                    float val = dataF[i][x];
                    if (val < min[i]) {
                        min[i] = val;
                    }
                    if (val > max[i]) {
                        max[i] = val;
                    }
                }
            } else {
                for (int i = 0; i < bandNum; i++)
                    dataF[i][x] = Float.NaN;
            }
        }

        if (validNumber == n) {
            convertTo256Data_PercentLinear(dataF, dat);
            return;
        }

        float[] diap = max.clone();
        for (int i = 0; i < diap.length; i++)
            diap[i] -= min[i];

        if (linTransformThreshold > 0) {
            // Form approximate histogram
            int[][] hist = new int[diap.length][histAccuracy];

            for (int i = 0; i < bandNum; i++) {
                if (diap[i] <= 0) {
                    hist[i][0] += n;
                } else {
                    float minBand = min[i];
                    float diapBand = diap[i];
                    int[] histBand = hist[i];
                    float[] dataBandF = dataF[i];
                    for (int x = 0; x < n; x++) {
                        if (! Float.isNaN(dataBandF[x])) {
                            histBand[Math.round((dataBandF[x] - minBand) / diapBand * (histAccuracy - 1))]++;
                        }
                    }
                }
            }

            // Change max/min, (max-min) values cutting about (threshold) data from each side
            int summ;
            int thr = (int) (linTransformThreshold * validNumber);
            for (int b = 0; b < hist.length; b++) {
                if (diap[b] == 0) {
                    continue;
                }

                float minBand = min[b];
                float diapBand = diap[b];
                int[] histBand = hist[b];

                summ = 0;
                for (int i = 0; i < histAccuracy; i++) {
                    if (summ + histBand[i] > thr) {
                        min[b] = i * diapBand / (histAccuracy - 1) + minBand;
                        break;
                    }
                    summ += histBand[i];
                }

                summ = 0;
                for (int i = histAccuracy - 1; i >= 0; i--) {
                    if (summ + histBand[i] > thr) {
                        max[b] = i * diapBand / (histAccuracy - 1) + minBand;
                        break;
                    }
                    summ += histBand[i];
                }
            }
            for (int i = 0; i < diap.length; i++)
                diap[i] = max[i] - min[i];
        }


        // x' = (x-x_min)/diap*255; or 0 if diap==0 or if x is not from [min, max]
        short[][] dataPoints = dat.getDataPoints();
        for (int i = 0; i < bandNum; i++) {
            short[] dataPointsBand = dataPoints[i];
            if (diap[i] <= 0) {
                for (int x = 0; x < n; x++)
                    dataPointsBand[x] = 0;
            } else {
                float minBand = min[i];
                float diapBand = diap[i];
                float[] dataBandF = dataF[i];
                // ����� �� ������� � 0
                short maskValue = (short) Math.round(- minBand / diapBand * 255);
                if (maskValue < 0) {
                    maskValue = 0;
                } else {
                    maskValue = 255;
                }
                for (int x = 0; x < n; x++) {
                    if (! Float.isNaN(dataBandF[x])) {
                        int val = Math.round((dataBandF[x] - minBand) / diapBand * 255);

                        if (val < 0) {
                            val = 0;
                        } else if (val > 255) {
                            val = 255;
                        }

                        dataPointsBand[x] = (short) val;
                    } else {
                        dataPointsBand[x] = maskValue;
                    }
                }
            }
        }

    }

    /**
     * Converts limited data in floats to integer [0,255] range and saves it in the given Data object.
     * Conversion is linear.
     *
     * @param dataF - array of float values with data
     * @param dat   - Data object to be filled with data
     */
    public void convertTo256Data_Linear(float[][] dataF, Data dat) {
        convertTo256Data_PercentLinear(dataF, dat, 0);
    }

    /**
     * Converts limited data in floats to integer [0,255] range and saves it in the given Data object.
     * Conversion is linear with (linTransformParam) crop from each side.
     *
     * @param dataF - array of float values with data
     * @param dat   - Data object to be filled with data
     */
    public void convertTo256Data_PercentLinear(float[][] dataF, Data dat) {
        convertTo256Data_PercentLinear(dataF, dat, linTransformParam);
    }

    /**
     * Converts limited data in floats to integer [0,255] range and saves it in the given Data object.
     * Conversion is linear with linTransformThreshold crop from each side.
     *
     * @param dataF                 - array of float values with data
     * @param dat                   - Data object to be filled with data
     * @param linTransformThreshold - linear transform threshold parameter
     */
    public void convertTo256Data_PercentLinear(float[][] dataF, Data dat, final float linTransformThreshold) {
        // dataF[bands][n];
        final int n = dataF[0].length;    // = dat.getNData();

        // Form max/min, (max-min) values for each band
        float[] min = new float[dataF.length];
        for (int i = 0; i < min.length; i++)
            min[i] = dataF[i][0];
        float[] max = min.clone();

        for (int i = 0; i < min.length; i++) {
            float minBand = min[i];
            float maxBand = max[i];
            float[] dataBandF = dataF[i];
            for (int x = 0; x < n; x++) {
                if (dataBandF[x] < minBand) {
                    minBand = dataBandF[x];
                }
                if (dataBandF[x] > maxBand) {
                    maxBand = dataBandF[x];
                }
            }
            min[i] = minBand;
            max[i] = maxBand;
        }

        float[] diap = max.clone();
        for (int i = 0; i < diap.length; i++)
            diap[i] -= min[i];

        if (linTransformThreshold > 0) {
            // Form approximate histogram
            int[][] hist = new int[diap.length][histAccuracy];
            for (int i = 0; i < min.length; i++) {
                if (diap[i] <= 0) {
                    hist[i][0] += n;
                } else {
                    float minBand = min[i];
                    float diapBand = diap[i];
                    int[] histBand = hist[i];
                    float[] dataBandF = dataF[i];
                    for (int x = 0; x < n; x++)
                        histBand[Math.round((dataBandF[x] - minBand) / diapBand * (histAccuracy - 1))]++;
                }
            }

            // Change max/min, (max-min) values cutting about (threshold) data from each side
            int summ;
            int thr = (int) (linTransformThreshold * n);
            for (int b = 0; b < hist.length; b++) {
                if (diap[b] == 0) {
                    continue;
                }

                final float minBand = min[b];
                final float diapBand = diap[b];
                int[] histBand = hist[b];

                summ = 0;
                for (int i = 0; i < histAccuracy; i++) {
                    if (summ + histBand[i] > thr) {
                        min[b] = i * diapBand / (histAccuracy - 1) + minBand;
                        break;
                    }
                    summ += histBand[i];
                }

                summ = 0;
                for (int i = histAccuracy - 1; i >= 0; i--) {
                    if (summ + histBand[i] > thr) {
                        max[b] = i * diapBand / (histAccuracy - 1) + minBand;
                        break;
                    }
                    summ += histBand[i];
                }
            }
            for (int i = 0; i < diap.length; i++)
                diap[i] = max[i] - min[i];
        }

        // x' = (x-x_min)/diap*255; or 0 if diap==0 or if x is not from [min, max]
        short[][] dataPoints = dat.getDataPoints();
        for (int i = 0; i < min.length; i++) {
            short[] dataPointsBand = dataPoints[i];
            if (diap[i] <= 0) {
                for (int x = 0; x < n; x++)
                    dataPointsBand[x] = 0;
            } else {
                final float minBand = min[i];
                final float diapBand = diap[i];
                float[] dataBandF = dataF[i];
                for (int x = 0; x < n; x++) {
                    int val = Math.round((dataBandF[x] - minBand) / diapBand * 255);

                    if (val < 0) {
                        val = 0;
                    } else if (val > 255) {
                        val = 255;
                    }

                    dataPointsBand[x] = (short) val;
                }
            }
        }
    }


    // ADDITIONAL

    protected int[][] getDifferentColors_arr(int clNum) {
        // dim = 3;
        int[][] cl = new int[clNum][3];

        // ����� 3� ������ ������������ ������ [0, 255]^3 �� ��������� ��� ������� ������
        int freq = 1;
        while (power(freq, 3) < clNum) {
            freq++;
        }
        if (freq > 256) {
            freq = 256;
        }

        // �������� ����� ��� ������ ����������
        int h = 256 / freq;
        for (int i = 0; i < clNum; i++) {
            int ind = i;
            for (int d = 2; d >= 0; d--) {
                cl[i][d] = ind / power(freq, d);
                ind = ind % power(freq, d);
            }

            for (int d = 0; d < 3; d++)
                cl[i][d] = cl[i][d] * h + h / 2;
        }

        return cl;
    }

    protected int[] getDifferentColors_int(int clNum) {
        int[][] cl = getDifferentColors_arr(clNum);

        int[] cols = new int[clNum];
        for (int i = 0; i < clNum; i++)
            cols[i] = (255 << 24) | (cl[i][0] << 16) | (cl[i][1] << 8) | cl[i][2];

        return cols;
    }

    private static int power(int a, int b) {
        if (b < 0) {
            return 0;
        }

        int res = 1;
        for (int i = 0; i < b; i++)
            res *= a;

        return res;
    }

}
