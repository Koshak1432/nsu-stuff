package kosh;

import kosh.kmeans.Cluster;

import java.util.ArrayDeque;
import java.util.Queue;

public class AreaFiller {

    // change area pixels class to resClass
    public static Cluster fillArea(int insidePixel, short[] assignment, short resClass, int width) {
        Cluster recolored = new Cluster();
        int curIdx;
        int aClass = assignment[insidePixel];
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(insidePixel);
        while (!queue.isEmpty()) {
            curIdx = queue.poll();
            if (assignment[curIdx] == aClass) {
                assignment[curIdx] = resClass;
                recolored.addToRelatedPoints(curIdx);
                if (curIdx - 1 >= 0) {
                    queue.add(curIdx - 1);
                }
                if (curIdx + 1 < assignment.length) {
                    queue.add(curIdx + 1);
                }
                if (curIdx - width >= 0) {
                    queue.add(curIdx - width);
                }
                if (curIdx + width < assignment.length) {
                    queue.add(curIdx + width);
                }
            }
        }
        return recolored;
    }
}
