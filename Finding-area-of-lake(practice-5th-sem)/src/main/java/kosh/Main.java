package kosh;

import kosh.kmeans.Cluster;
import kosh.kmeans.Kmeans;
import kosh.display.ImageConstructor;
import kosh.display.ImageWindow;
import kosh.formaters.GdalFormater_M;
import kosh.parsing.ArgsParser;
import kosh.parsing.ParsedArgs;
import kosh.util.Util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println(USAGE);
            return;
        }
        ParsedArgs parsed = ArgsParser.parseArgs(args);
        if (parsed == null) {
            System.err.println(USAGE);
            return;
        }
        if (parsed.fileName() == null) {
            System.err.println("Couldn't get file name");
            System.err.println(USAGE);
            return;
        }
        System.out.println(parsed.fileName());

        File[] files = ArgsParser.getFileNames(parsed.fileName());
        int k = parsed.k();

        GdalFormater_M formater = new GdalFormater_M();
        try {
            if (!formater.loadHeader_M(files)) {
                System.err.println("Couldn't load header");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        boolean[] activeBands = new boolean[formater.getBandsNumber_M()];
        int activeNum;
        BandsAsRGB colorsDistribution;

        System.out.println("Available bands up to  " + formater.getBandsNumber_M());
        System.out.println("Select bands to load:");
        try (Scanner scanner = new Scanner(System.in)) {
            // какие грузить
            activeNum = ArgsParser.parseBandsToLoad(activeBands, scanner, formater.getBandsNumber_M());
            System.out.println("Available bands to show: " + Arrays.toString(
                    ArgsParser.getAvailableBandsToShow(activeBands, activeNum)));
            System.out.println("Select bands to show(R G B)");
            //какие показывать
            colorsDistribution = ArgsParser.parseBandsToShow(activeBands, scanner, formater.getBandsNumber_M());
            if (colorsDistribution == null) {
                System.err.println("Couldn't parse bands to show");
                return;
            }
        }

        Data data = formater.loadData_M(activeBands);
        // отображает номер канала(in general) в локальный номер канала, тот что в 1d dataPoints
        int[] bandsDistribution = Util.getGeneralToLocalDistribution(activeBands, activeNum);
        data.setColorDistribution(colorsDistribution, bandsDistribution);

        Kmeans kMeans = new Kmeans(data.getDataPoints(), k);
        int iterations = 1;
        if (!kMeans.run(iterations)) {
            System.err.println("Error while running k-means");
            return;
        }

        short[] clusteringAssignment = kMeans.getAssignment();
        List<Cluster> clusters = kMeans.getClusters();
        findAndAddWaterClasses(data, clusteringAssignment, clusters);
        // найти пиксель озера: для снимков, где озеро слева снизу x = 1100, справа x = 4500, y = 5700, т.е. idx = y * width + x
        addFilledAreaToClusters(data, clusteringAssignment, clusters);

        data.setClusters(clusters);
        data.setClassificationAssignment(clusteringAssignment);

        int lakeArea = calculateArea(clusteringAssignment, clusters.size());
        System.out.println("lake area(meters^2): " + lakeArea);

        // size - 1 -- lake, size - 2 -- waterClasses
        BufferedImage clustersColorsClusteringImg = ImageConstructor.constructImageByClustersColors(data, clusters.size() - 2);
        BufferedImage beforeClusteringImg = ImageConstructor.constructImage(data);

        ImageWindow beforeClustering = new ImageWindow(beforeClusteringImg, "Source img");
        ImageWindow meanColors = new ImageWindow(clustersColorsClusteringImg, "Result img");

        File outFile = new File("result.png");
        if (!Util.saveImg(outFile, clustersColorsClusteringImg)) {
            System.err.println("Couldn't save img to " + outFile.getPath());
        }

        File classificationOut = new File("result");
        if (!formater.saveClassification(classificationOut, data, "HFA", null,
                                    Util.getRandomColors(data.getNumberOfClusters()))) {
            System.err.println("Couldn't save classification");
        }
    }

    private static void addFilledAreaToClusters(Data data, short[] clusteringAssignment, List<Cluster> clusters) {
        int x = 1100;
        int y = 5700;
        int lakeIdx = y * data.getWidth() + x;
        clusters.add(AreaFiller.fillArea(lakeIdx, clusteringAssignment, (short) clusters.size(), data.getWidth()));
    }

    private static void findAndAddWaterClasses(Data data, short[] clusteringAssignment, List<Cluster> clusters) {
        Set<Short> waterClasses = NDWIcalculator.getWaterClasses(clusters,
                                                                 data.getBandByDescription("3"),
                                                                 data.getBandByDescription("5"));
        if (waterClasses == null) {
            System.err.println("Couldn't calculate NDWI, load nir(5) and green(3) channels");
        } else {
            addWaterClasses(clusteringAssignment, clusters, waterClasses);
        }
    }

    private static int calculateArea(short[] clusteringAssignment, int clustersSize) {
        int pixelArea = 30 * 30;
        int lakePixels = 0;
        for (short value : clusteringAssignment) {
            if (value == clustersSize - 1) {
                ++lakePixels;
            }
        }
        return lakePixels * pixelArea;
    }

    private static void addWaterClasses(short[] clusteringAssignment, List<Cluster> clusters, Set<Short> waterClasses) {
        Cluster waterCl = new Cluster();
        for (int i = 0; i < clusteringAssignment.length; ++i) {
            if (waterClasses.contains(clusteringAssignment[i])) {
                waterCl.addToRelatedPoints(i);
                clusteringAssignment[i] = (short) clusters.size();
            }
        }
        clusters.add(waterCl);
    }

    private static final String USAGE = "Gimme args: -f<file_to_open> -k<num_of_clusters>";
}