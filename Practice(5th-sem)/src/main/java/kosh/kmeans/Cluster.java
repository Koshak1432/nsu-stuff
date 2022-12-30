package kosh.kmeans;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    public Cluster(short[] point) {
        bandsMeans = point; // взял какой-то пиксель
    }
    public Cluster() {}

    public void addToRelatedPoints(int point) {
        relatedPointsCoords.add(point);
    }

    public short[] getBandsMeans() {
        return bandsMeans;
    }

    public List<Integer> getRelatedPointsCoords() {
        return relatedPointsCoords;
    }

    private final List<Integer> relatedPointsCoords = new ArrayList<>();
    private short[] bandsMeans = new short[0]; // центр масс
}
