package kosh;

import kosh.kmeans.Cluster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NDWIcalculator {
    public static Set<Short> getWaterClasses(List<Cluster> clusters, short[] green, short[] nir) {
        if (green == null || nir == null) {
            return null;
        }
        Set<Short> waterClasses = new HashSet<>();
        for (short i = 0; i < clusters.size(); ++i) {
            List<Integer> points = clusters.get(i).getRelatedPointsCoords();
            double sum = 0;
            for (Integer idx : points) {
                sum += getNDWI(idx, green, nir);
            }
            double res = sum / points.size();
            System.out.println("NDWI res " + i + ": " + res);
            if (res > 0.28) {
                waterClasses.add(i);
            }
        }
        return waterClasses;
    }
    private static double getNDWI(int idx, short[] green, short[] nir) {
        return (double)(green[idx] - nir[idx]) / (green[idx] + nir[idx]);
    }
}
