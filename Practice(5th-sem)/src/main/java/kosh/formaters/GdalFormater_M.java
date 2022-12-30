package kosh.formaters;

import java.awt.Color;
import java.io.File;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import kosh.Data;
import kosh.formaters.AFileFormater;
import org.gdal.gdal.Band;
import org.gdal.gdal.ColorTable;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

public class GdalFormater_M extends AFileFormater {
    /* New features */
    // _M:
    //   instead of implementing an interface it extends AFileFormater, which can open number of files as one data
    //   everything with linTransform and some additional methods were extracted to AFileFormater

    private Dataset poDataset;
    private Hashtable props;
    private int width, height, numBands = 1;
    private double[] geoTransform;
    private double resolution = 0;    // = geoTransform[1]
    private ColorTable colorTable;
    private Vector classNames;
    private File lastFile;

    boolean saveRawDataMode = false;
    private float[][] rawData;    // [band][ y*samples+x ]


    /**
     * Creates default GdalFormater object.
     */
    public GdalFormater_M() {
        gdal.AllRegister();
    }


    public void setSaveRawDataMode(boolean mode) {
        saveRawDataMode = mode;
        if (! saveRawDataMode) {
            rawData = null;
        }
    }

    public boolean isRawDataAvaible(File file, int width, int height) {
        return rawData != null && width == this.width && height == this.height && file.equals(lastFile);
    }

    public float getRawDataPoint(int x, int y, int band) {
        if (rawData == null || x < 0 || y < 0 || band < 0 || band >= rawData.length || x >= width || y >= height) {
            return Float.NaN;
        }
        return rawData[band][y * width + x];
    }

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
        if ((newValue < 0) || (newValue > 0.99f)) {
            return false;
        }
        linTransformParam = newValue;
        return true;
    }

    /**
     * @return loaded data width or default value = 0
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return loaded data height or default value = 0
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return number of spectral bands in the loaded data or default value = 1
     */
    public int getBandsNumber() {
        return numBands;
    }

    /**
     * @return image resolution in meters or 0 if unknown
     */
    public double getResolution() {
        return resolution;
    }

    /**
     * @return last opened file or Null
     */
    public File getLastFile() {
        if (lastFile == null) {
            return null;
        } else {
            return new File(lastFile.getAbsolutePath());
        }
    }

    /**
     * Searches for the property with the specified key in the inner Properties object,
     * witch should be filled by loadHeader() method.
     * The method returns null if the property is not found.
     *
     * @param key - key value
     * @return the value in the current property list with the specified key value, or null if the property is not found
     */
    public String getProperty(String key) {
        return (String) props.get(key);
    }

    /**
     * Prints all the properties of the inner Properties object to the specified output stream.
     *
     * @param output - an output stream.
     */
    public void printCurrentProperties(PrintStream output) {
        if (props.isEmpty()) {
            output.println("No loaded properties.");
        } else {
            Enumeration keys = props.keys();
            output.println(props.size() + " items of metadata:");
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                output.println(" " + key + " = " + props.get(key));
            }
            output.println();
        }
    }

    /**
     * @return list of bands descriptions or just one element "1."
     */
    public String[] getBandsDescription() {
        // In case of normal image
        if (poDataset != null) {
            String fileType = poDataset.GetDriver().getShortName();
            if (fileType.equals("PNG") || fileType.equals("JPEG") || fileType.equals("GIF") || fileType.equals(
                    "BMP") || fileType.equals("JP2OpenJPEG")) {
                if (numBands == 4) {
                    String[] bandDesc = {"Red", "Green", "Blue", "Alpha"};
                    return bandDesc;
                } else if (numBands == 3) {
                    String[] bandDesc = {"Red", "Green", "Blue"};
                    return bandDesc;
                }
            }
        }

        // General case
        String[] bandDesc = new String[numBands];
        for (int i = 1; i <= numBands; i++) {
            String bandName = getProperty("Band_" + i);
            if ((bandName == null) || (bandName.length() == 0)) {
                bandName = Integer.toString(i);
            }
            bandDesc[i - 1] = bandName;
        }
        return bandDesc;
    }

    public Color[] getColorTable() {
        if (colorTable == null) {
            return null;
        }
        int numColors = colorTable.GetCount();
        if (numColors == 0) {
            return null;
        }
        Color[] colors = new Color[numColors];
        for (int i = 0; i < numColors; i++)
            colors[i] = colorTable.GetColorEntry(i);

        return colors;
    }

    public String[] getClassNames() {
        if ((classNames == null) || (classNames.size() == 0)) {
            return null;
        }
        return (String[]) classNames.toArray();
    }


    // OPEN FILE METHODS

    /**
     * Looking for the data file.
     * If file is in ENVI header format, looking for it's data file.
     *
     * @param dataFile - data file to be checked
     * @return Data file that can be read or null
     */
    private File lookForDataFile(File dataFile) {
        if ((dataFile == null) || (! dataFile.canRead()) || (! dataFile.isFile())) {
            return null;
        }

        // For opening ENVI file, not header, but data file must be specified
        String name = dataFile.getName();
        if (name.endsWith(".hdr") || name.endsWith(".HDR")) {
            File f = new File(dataFile.getParent(), name.substring(0, name.length() - 4));
            if (f.canRead()) {
                return f;
            }

            File dataFile_dat = new File(f.getParent(), f.getName() + ".dat");
            if (dataFile_dat.canRead()) {
                return dataFile_dat;
            }

            File dataFile_txt = new File(f.getParent(), f.getName() + ".txt");
            if (dataFile_txt.canRead()) {
                return dataFile_txt;
            }

            return null;
        } else {
            return dataFile;
        }
    }

    public boolean loadHeader(File file) {
        return load(file);
    }

    /**
     * Loading file info.
     *
     * @param file - file
     * @return true if loading was successful; false otherwise
     */
    private boolean load(File file) {
        File f = lookForDataFile(file);
        if (f == null) {
            System.out.println("Data file is not readable!");
            return false;
        }

        //if (poDataset != null)
        //	poDataset.delete();

        try {
            poDataset = gdal.Open(f.getAbsolutePath(), gdalconst.GA_ReadOnly);
            if (poDataset == null) {
                System.out.println("The image could not be loaded.");
                printLastError();
                return false;
            }
        } catch (Exception e) {
            System.out.println("The image could not be loaded.");
            System.err.println(e.getMessage());
            e.printStackTrace();
            return false;
        }

//		System.out.println("Loading driver: " + poDataset.GetDriver().GetDescription());

        width = poDataset.getRasterXSize();
        height = poDataset.getRasterYSize();
        numBands = poDataset.getRasterCount();
        props = poDataset.GetMetadata_Dict("");        // Band names stored here
        classNames = null;
        colorTable = null;

        geoTransform = poDataset.GetGeoTransform();
        // geoTransform [2] and [4] = 0, ���� ������ �������� �� ������. ����� �� ������, ��� ���������� ����� ������.
        if (poDataset.GetProjection().equals("") || geoTransform[2] != 0 || geoTransform[4] != 0) {
            resolution = 0;
        } else {
            double pixelWidth = Math.abs(geoTransform[1]);
            double pixelHeight = Math.abs(geoTransform[5]);
            if (pixelWidth == pixelHeight) {
                resolution = pixelWidth;
            } else {
                resolution = Math.sqrt(pixelWidth * pixelHeight);    // �������, �� ������� ������� ���������
            }
        }


        // Maybe classification was loaded
        if (numBands == 1) {
            Band b = poDataset.GetRasterBand(1);
            // Get class names, if there are any
            classNames = b.GetRasterCategoryNames();
            if ((classNames != null) && (classNames.size() > 0)) {
                System.out.println("File has " + classNames.size() + " category names");
            }
            // Get class colors, if there are any
            if ((b.GetRasterColorInterpretation() == gdalconst.GCI_PaletteIndex) && (b.GetRasterColorTable() != null)) {
                colorTable = b.GetRasterColorTable();
                System.out.println("File has color table for " + colorTable.GetCount() + " classes");
            }
        }

        lastFile = file;
        rawData = null;
        return true;
    }

    /**
     * Loading data-file into the new Data object.
     *
     * @param activeBands - selected bands to load, or all bands if null
     * @return Data object filled with data from file; or null if loading was not successful
     */
    public Data loadData(boolean[] activeBands) {
        // Some checks
        if (poDataset == null) {
            System.out.println("File was not loaded");
            return null;
        }
        if (activeBands == null) {
            activeBands = new boolean[numBands];
            Arrays.fill(activeBands, true);
        }
        if (numBands != activeBands.length) {
            System.out.println("Invalide selected bands!");
            return null;
        }
        int activeNumber = 0;
        for (int i = 0; i < numBands; i++)
            if (activeBands[i]) {
                activeNumber++;
            }
        if (activeNumber == 0) {
            System.out.println("Invalide selected bands!");
            return null;
        }

        /* Variants */

        // 1) Load classification
        if ((colorTable != null) && (colorTable.GetCount() > 0) && (numBands == 1)) {
            return loadClassification();
        } else if ((classNames != null) && (classNames.size() > 0) && (numBands == 1)) {
            return loadClassification();
        }

        // 2) Normal image files like png, jpg, ... direct load, no linear transform
        String fileType = poDataset.GetDriver().getShortName();
        if (fileType.equals("PNG") || fileType.equals("JPEG") || fileType.equals("GIF") || fileType.equals(
                "BMP") || fileType.equals("JP2OpenJPEG")) {
            return directLoadData(activeBands);
        }

        // 3) General case
        // load raw data in float
        float[][] dataF = loadData_float(activeBands);
        if (dataF == null) {
            return null;
        }
        // form data object
        String fileName = lastFile.getName();
        int dotInd = fileName.lastIndexOf(".");
        if ((dotInd > 0) && (fileName.length() - dotInd < 6)) {
            fileName = fileName.substring(0, dotInd);
        }
        Data dat = new Data(width, height, activeNumber, fileName);
        String[] bandsDesc = getBandsDescription();
        int bInd = 0;
        for (int i = 0; i < activeBands.length; i++)
            if (activeBands[i]) {
                dat.setBandDescription(bInd++, bandsDesc[i]);
            }
        dat.setResolution(resolution);
        // form data applying linear transform
        convertTo256Data_PercentLinear_BlackMask(dataF, dat);
        //convertTo256Data_PercentLinear(dataF, dat);

        poDataset.FlushCache();

        if (saveRawDataMode) {
            rawData = dataF;
        }
        return dat;
    }

    public float[][] loadData_float(boolean[] activeBands) {
        if (poDataset == null) {
            System.out.println("File was not loaded");
            return null;
        }
        if (activeBands == null) {
            activeBands = new boolean[numBands];
            for (int i = 0; i < activeBands.length; i++)
                activeBands[i] = true;
        }
        if (numBands != activeBands.length) {
            System.out.println("Invalide selected bands!");
            return null;
        }
        int activeNumber = 0;
        for (int i = 0; i < numBands; i++)
            if (activeBands[i]) {
                activeNumber++;
            }
        if (activeNumber == 0) {
            System.out.println("Invalide selected bands!");
            return null;
        }

        ByteBuffer[] bands = new ByteBuffer[activeNumber];

        int xsize = poDataset.getRasterXSize();
        int ysize = poDataset.getRasterYSize();
        int pixels = xsize * ysize;
        int buf_type = 0, buf_size = 0;

        int band = 0;
        for (int b = 0; b < numBands; b++) {
            if (! activeBands[b]) {
                continue;
            }

            /* Bands are not 0-base indexed, so we must add 1 */
            Band poBand = poDataset.GetRasterBand(b + 1);

            buf_type = gdalconst.GDT_Float32;    // poBand.getDataType();
            buf_size = pixels * (gdal.GetDataTypeSize(buf_type) / 8);
            ByteBuffer data = ByteBuffer.allocateDirect(buf_size);
            data.order(ByteOrder.nativeOrder());

            int returnVal = 0;
            try {
                returnVal = poBand.ReadRaster_Direct(0, 0, xsize, ysize, xsize, ysize, buf_type, data);
            } catch (Exception ex) {
                System.err.println("Could not read raster data.");
                System.err.println(ex.getMessage());
                ex.printStackTrace();
                return null;
            }
            if (returnVal == gdalconstConstants.CE_None) {
                bands[band] = data;
            } else {
                printLastError();
            }

            band++;
        }

        float[][] dataF = new float[activeNumber][];
        for (int i = 0; i < activeNumber; i++) {
            dataF[i] = new float[pixels];
            bands[i].asFloatBuffer().get(dataF[i]);
        }

        if (saveRawDataMode) {
            rawData = dataF;
        }
        return dataF;
    }

    private Data directLoadData(boolean[] activeBands) {
        // Checks were made before
        if (activeBands == null) {
            activeBands = new boolean[numBands];
            for (int i = 0; i < activeBands.length; i++)
                activeBands[i] = true;
        }
        int activeNumber = 0;
        for (int i = 0; i < numBands; i++)
            if (activeBands[i]) {
                activeNumber++;
            }

        // Form data object
        String fileName = lastFile.getName();
        int dotInd = fileName.lastIndexOf(".");
        if ((dotInd > 0) && (fileName.length() - dotInd < 6)) {
            fileName = fileName.substring(0, dotInd);
        }
        Data dat = new Data(width, height, activeNumber, fileName);
        String[] bandsDesc = getBandsDescription();
        int bInd = 0;
        for (int i = 0; i < activeBands.length; i++)
            if (activeBands[i]) {
                dat.setBandDescription(bInd++, bandsDesc[i]);
            }

        short[][] bands = dat.getDataPoints();

        int band = 0;
        for (int b = 0; b < numBands; b++) {
            if (! activeBands[b]) {
                continue;
            }
            Band poBand = poDataset.GetRasterBand(b + 1);

            int returnVal = 0;
            try {
                returnVal = poBand.ReadRaster(0, 0, width, height, width, height, gdalconst.GDT_Int16, bands[band]);
            } catch (Exception ex) {
                System.err.println("Could not read raster data.");
                System.err.println(ex.getMessage());
                ex.printStackTrace();
                return null;
            }
            if (returnVal != gdalconstConstants.CE_None) {
                printLastError();
                return null;
            }

            band++;
        }

        dat.setResolution(resolution);

        return dat;
    }

    private Data loadClassification() {
        // Checks were made before

        // load data
        Data dat = directLoadData(null);

        // data values must be in [0; numCl-1] range
        int numCl;
        // if ((colorTable != null) && (colorTable.GetCount() > 0))
        if ((classNames != null) && (classNames.size() > 0)) {
            numCl = classNames.size();
        } else {
            numCl = colorTable.GetCount();
        }

        short[] band = dat.getDataPoints()[0];
        short[] classes = dat.getClassificationAssignment();
        System.arraycopy(band, 0, classes, 0, band.length);
//		dat.setNumberOfClusters(numCl);

        // ����� �������� �������� ������� �������

        float coef = 255.f / (numCl - 1);
        for (int i = 0; i < band.length; i++) {
            short val = (short) (band[i] * coef);
            if (val < 0) {
                val = 0;
            }
            if (val > 255) {
                val = 255;
            }
            band[i] = val;
        }

        return dat;
    }

    /**
     * Loading data from file into the new Data object.
     * Loading only elements, selected in the given mask.
     *
     * @param activeBands - selected bands to load, or all bands if null
     * @param mask        - mask for loading some selected part of data
     * @param selectedInd - mask value for selected elements to be loaded
     * @return Data object filled with selected data from file; or null if loading was not successful
     */
    public Data loadData_mask(boolean[] activeBands, short[] mask, int selectedInd) {
        // Some checks
        if (mask.length != width * height) {
            System.out.println("Given mask doesn't correspond to the current data file!");
            return null;
        }
        if (poDataset == null) {
            System.out.println("File was not loaded");
            return null;
        }
        if (activeBands == null) {
            activeBands = new boolean[numBands];
            for (int i = 0; i < activeBands.length; i++)
                activeBands[i] = true;
        }
        if (numBands != activeBands.length) {
            System.out.println("Invalide selected bands!");
            return null;
        }
        int activeNumber = 0;
        for (int i = 0; i < numBands; i++)
            if (activeBands[i]) {
                activeNumber++;
            }
        if (activeNumber == 0) {
            System.out.println("Invalide selected bands!");
            return null;
        }

        // Load raw data in float
        float[][] dataF = loadData_float(activeBands);
        if (dataF == null) {
            return null;
        }

        // Choose elements selected in mask
        int n = 0;
        for (int i = 0; i < mask.length; i++)
            if (mask[i] == selectedInd) {
                n++;
            }
        float[][] dataFMask = new float[activeNumber][n];
        int ind = 0;
        for (int i = 0; i < mask.length; i++)
            if (mask[i] == selectedInd) {
                for (int b = 0; b < activeNumber; b++)
                    dataFMask[b][ind] = dataF[b][i];
                ind++;
            }

        // Form data object
        String fileName = lastFile.getName();
        int dotInd = fileName.lastIndexOf(".");
        if (dotInd > 0) {
            fileName = fileName.substring(0, dotInd);
        }
        Data dat = new Data(1, n, activeNumber, fileName + "_part");
        String[] bandsDesc = getBandsDescription();
        int bInd = 0;
        for (int i = 0; i < activeBands.length; i++)
            if (activeBands[i]) {
                dat.setBandDescription(bInd++, bandsDesc[i]);
            }

        // Form data applying linear transform
        convertTo256Data_PercentLinear(dataFMask, dat);

        return dat;
    }


    // SAVE

    public boolean saveClassification(File file, Data resultData, String driverName, String description,
                                      int[] clColors) {
        return saveClassification(file, resultData, driverName, description, clColors, null);
    }

    /**
     * Saving classification result in file.
     *
     * @param file        - file to save classification
     * @param resultData  - Data object containing classification result
     * @param driverName  - file type, e.g.: "ENVI", "HFA" for (.img). Set to ENVI if null.
     * @param description - string that contains description of the applied classification method, or null
     * @param clColors    - colors for the classes, or null
     * @param classNames  - colors for the classes, or just null
     * @return true if the file was saved successfully; false otherwise
     */
    public boolean saveClassification(File file, Data resultData, String driverName, String description, int[] clColors,
                                      String[] classNames) {
        if (file == null || resultData == null) {
            return false;
        }
        int clNum = resultData.getNumberOfClusters();
        if ((clNum <= 0) || (clNum > 255)) {
            return false;
        }

//		if (description == null)
//			description = resultData.getAlgorithmName();
        if (description == null) {
            description = "";
        }

        if (driverName == null) {
            driverName = "ENVI";
        }

        // If last loaded file corresponds resultData, try to save meta, georef and so on
        int h = resultData.getHeight();
        int w = resultData.getWidth();
        boolean saveMeta = width == w && height == h;

        if (driverName.equals("ENVI")) {
            String name = file.getName();
            if (name.endsWith(".hdr") || name.endsWith(".HDR")) {
                file = new File(file.getParent(), name.substring(0, name.length() - 4));
            }
        }
        if (driverName.equals("HFA")) {
            file = new File(file.getParent(), file.getName() + ".img");
        }

        // delete '-1' "noise" cluster;
        short[] classNumber = resultData.getClassificationAssignment().clone();
        //resultData.setNumberOfClusters(clNum+1);
        for (int i = 0; i < classNumber.length; i++)
            classNumber[i]++;

        // Form file
        Driver drv = gdal.GetDriverByName(driverName);
        Dataset resultDataset = drv.Create(file.getAbsolutePath(), w, h, 1, gdalconst.GDT_Byte);
        resultDataset.SetDescription(description);
        Band clBand = resultDataset.GetRasterBand(1);
        clBand.SetDescription(description);

        // Add class names
        Vector<String> clNames = new Vector<String>();
        if ((classNames == null) || (classNames.length != clNum + 1) || (clNum != clColors.length)) {
            clNames.add("Unclassified");
            for (int i = 1; i <= clColors.length; i++)
                clNames.add("Class " + i);
        } else {
            if (classNames[0] == null) {
                clNames.add("Unclassified");
            } else {
                clNames.add(classNames[0]);
            }
            for (int i = 1; i <= clNum; i++) {
                if (classNames[i] == null) {
                    clNames.add("Class " + i);
                } else {
                    clNames.add(classNames[i]);
                }
            }
        }
        clBand.SetCategoryNames(clNames);

        // Add class colors
        if ((clColors == null) || (clColors.length != clNum)) {
            clColors = getDifferentColors_int(clNum);
        }
        clBand.SetRasterColorInterpretation(gdalconst.GCI_PaletteIndex);
        ColorTable ct = new ColorTable(gdalconst.GPI_RGB);
        ct.SetColorEntry(0, Color.black);
        for (int i = 0; i < clNum; i++)
            ct.SetColorEntry(i + 1, new Color(clColors[i]));
        clBand.SetRasterColorTable(ct);

        if (saveMeta) {
            resultDataset.SetGeoTransform(poDataset.GetGeoTransform());
            resultDataset.SetProjection(poDataset.GetProjection());
        }

        // Write data
        int returnVal = 0;
        try {
            returnVal = resultDataset.WriteRaster(0, 0, w, h, w, h, gdalconst.GDT_Int16, classNumber, null);
        } catch (Exception ex) {
            System.err.println("Could not write raster data.");
            ex.printStackTrace();
            resultDataset.delete();
            return false;
        }
        if (returnVal == gdalconstConstants.CE_Failure) {
            System.err.println("Could not write raster data.");
            printLastError();
            resultDataset.delete();
            return false;
        }

        resultDataset.delete();
        return true;
    }

    /**
     * Saving raster data in its internal representation in file.
     *
     * @param file        - file to save data
     * @param resultData  - Data object containing raster to save
     * @param driverName  - file type, e.g.: "GTiff", "ENVI", "HFA" for (.img). Set to GTiff if null.
     * @param description - string that contains description of the data, or null
     * @return true if the file was saved successfully; false otherwise
     */
    public boolean saveRaster(File file, Data resultData, String driverName, String description) {
        return saveRaster(file, resultData, driverName, description, null);
    }

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
    public boolean saveRaster(File file, Data resultData, String driverName, String description, int[] bandOrder) {
        if (file == null || resultData == null) {
            return false;
        }
        int h = resultData.getHeight();
        int w = resultData.getWidth();
        int numBands = resultData.getNumBands();
        short[][] dat = resultData.getDataPoints();
        if ((w == 0) || (h == 0) || (numBands == 0)) {
            return false;
        }

        if (bandOrder != null) {
            if (bandOrder.length != numBands) {
                bandOrder = null;
            } else {
                for (int b : bandOrder)
                    if ((b < 0) || (b >= numBands)) {
                        bandOrder = null;
                        break;
                    }
            }
        }

//		if (description == null)
//			description = resultData.getImageName();
        if (description == null) {
            description = "GdalFormater";
        }

        if (driverName == null) {
            driverName = "GTiff";
        }

        // If last loaded file corresponds resultData, try to save meta, georef and so on
        boolean saveMeta = width == w && height == h;

        String fname = file.getName();
        if (driverName.equals("ENVI")) {
            if (fname.endsWith(".hdr") || fname.endsWith(".HDR")) {
                file = new File(file.getParent(), fname.substring(0, fname.length() - 4));
            }
        }
        if (driverName.equals("HFA")) {
            if (! fname.endsWith(".img") && ! fname.endsWith(".IMG")) {
                file = new File(file.getParent(), fname + ".img");
            }
        }
        if (driverName.equals("GTiff")) {
            if (! fname.endsWith(".tif") && ! fname.endsWith(".TIF") && ! fname.endsWith(".tiff") && ! fname.endsWith(
                    ".TIFF")) {
                file = new File(file.getParent(), fname + ".tif");
            }
        }

        // Form file
        Driver drv = gdal.GetDriverByName(driverName);
        Dataset resultDataset = drv.Create(file.getAbsolutePath(), w, h, numBands, gdalconst.GDT_Byte);
        resultDataset.SetDescription(description);
        if (saveMeta) {
            resultDataset.SetGeoTransform(poDataset.GetGeoTransform());
            resultDataset.SetProjection(poDataset.GetProjection());
        }

        // Set bands descriptions
        for (int b = 0; b < numBands; b++) {
            Band band = resultDataset.GetRasterBand(b + 1);
            int corrBand = b;
            if (bandOrder != null) {
                corrBand = bandOrder[b];
            }
            band.SetDescription(resultData.getBandDescription(corrBand));
        }


        // Write data
        for (int b = 0; b < numBands; b++) {
            int corrBand = b;
            if (bandOrder != null) {
                corrBand = bandOrder[b];
            }
            short[] bandDat = dat[corrBand];
            Band band = resultDataset.GetRasterBand(b + 1);

            int returnVal = 0;
            try {
                // returnVal = band.WriteRaster(0, 0, w, h, w, h, gdalconst.GDT_Int16, bandDat);
                returnVal = band.WriteRaster(0, 0, w, h, bandDat);
            } catch (Exception ex) {
                System.err.println("Could not write raster data.");
                ex.printStackTrace();
                resultDataset.delete();
                return false;
            }
            if (returnVal == gdalconstConstants.CE_Failure) {
                System.err.println("Could not write raster data.");
                printLastError();
                resultDataset.delete();
                return false;
            }
        }

        resultDataset.delete();
        return true;
    }


    // ADDITIONAL

    public void printLastError() {
        System.out.println("Last error: " + gdal.GetLastErrorMsg());
        System.out.println("Last error no: " + gdal.GetLastErrorNo());
        System.out.println("Last error type: " + gdal.GetLastErrorType());
    }

}
