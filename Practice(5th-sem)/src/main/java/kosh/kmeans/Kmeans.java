package kosh.kmeans;

import java.util.*;

public class Kmeans {
    public Kmeans(short[][] points, int k) {
        this.k = k;
        this.pixels = points[0].length;
        this.numBands = points.length;
        this.assignment = new short[pixels];
        this.points = new short[pixels][numBands];

        // maybe make 1d array
        for (int i = 0; i < pixels; ++i) {
            for (int j = 0; j < numBands; ++j) {
                this.points[i][j] = points[j][i];
            }
        }

        System.out.println("KMEANS:");
        System.out.println("Pixels: " + pixels);
        System.out.println("Num bands: " + numBands + ", k: " + k);
    }


    private void updateCluster(Cluster cluster) {
        int sum;
        short bandMean;
        List<Integer> relatedPoints = cluster.getRelatedPointsCoords();
        if (relatedPoints.size() == 0) {
            return;
        }
        for (int i = 0; i < numBands; ++i) {
            sum = 0;
            for (Integer pixelIdx : relatedPoints) {
                sum += points[pixelIdx][i];
            }
            bandMean = (short) (sum / relatedPoints.size());
            cluster.getBandsMeans()[i] = bandMean;
        }
    }

    private void initClusters() {
        Random random = new Random();
        Set<Integer> chosenIdxes = new HashSet<>();
        int idx;
        for (int i = 0; i < k; ++i) {
            do {
                idx = random.nextInt(pixels);
            } while (chosenIdxes.contains(idx));
            chosenIdxes.add(idx);
            clusters.add(new Cluster(Arrays.copyOf(points[idx], numBands)));
        }

    }

    private double calculateSSE() {
        double sum = 0;
        for (Cluster cluster : clusters) {
            for (Integer idx : cluster.getRelatedPointsCoords()) {
                sum += calculateDistance(points[idx], cluster.getBandsMeans());
            }
        }
        return sum;
    }

    private void addToClusters() {
        for (int i = 0; i < pixels; ++i) {
            clusters.get(assignment[i]).addToRelatedPoints(i);
        }
    }

    private void clearClusters() {
        for (Cluster cluster : clusters) {
            cluster.getRelatedPointsCoords().clear();
        }
    }

    public boolean run(int iterations) {
        System.out.println("Start k-means");
        if (iterations <= 0) {
            System.err.println("Num of iterations must be > 0");
            return false;
        }
        double bestSSE = Double.MAX_VALUE;
        double SSE;
        short[] bestAssignment = new short[0];
        List<Cluster> bestClusters = null;

        for (int i = 0; i < iterations; ++i) {
            long start = System.currentTimeMillis();
            SSE = kmeans();
            long end = System.currentTimeMillis();
            System.out.println("Time passed of " + i + " iteration(sec): " + (end - start) / 1000 + " , SSE: " + SSE);
            if (SSE < bestSSE) {
                bestSSE = SSE;
                bestAssignment = Arrays.copyOf(assignment, assignment.length);
                bestClusters = new ArrayList<>(clusters);
            }
        }
        System.out.println("Best sse: " + bestSSE);
        assignment = bestAssignment;
        clusters = bestClusters;
        return true;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public short[] getAssignment() {
        return assignment;
    }

    private double kmeans() {
        double SSE = Double.MAX_VALUE; // Sum of Squared Errors
        double minDistance;
        //select k init centroids
        initClusters();

        while (true) {
            clearClusters();
            for (int i = 0; i < pixels; ++i) {
                minDistance = Double.MAX_VALUE;
                short nearestClusterIdx = 0;
                for (short j = 0; j < clusters.size(); ++j) {
                    double dist = calculateDistance(points[i], clusters.get(j).getBandsMeans());
                    if (dist < minDistance) {
                        minDistance = dist;
                        nearestClusterIdx = j;
                    }
                }
                assignment[i] = nearestClusterIdx;
            }
            addToClusters();

            for (Cluster cluster : clusters) {
                updateCluster(cluster);
            }
            double newSSE = calculateSSE();
            if (SSE - newSSE <= PRECISION) {
                return newSSE;
            }
            SSE = newSSE;
        }
    }

    // a and b are bands arrays
    private double calculateDistance(short[] a, short[] b) {
        double sum = 0;
        for (int i = 0; i < numBands; ++i) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return sum;
    }


    private final int k;
    private final int numBands;
    private final int pixels;
    private final short[][] points; // dim 1 -- pixel idx, dim 2 -- bands
    private final double PRECISION = 0.001;
    private List<Cluster> clusters = new ArrayList<>();
    private short[] assignment;
}
