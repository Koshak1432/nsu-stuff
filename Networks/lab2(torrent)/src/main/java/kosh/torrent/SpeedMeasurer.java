package kosh.torrent;

import java.util.HashMap;
import java.util.Map;

public class SpeedMeasurer {
    public SpeedMeasurer() {};

    public void printIfNeed() {
        long current = System.currentTimeMillis();
        if (current - lastTime3Sec > timeToPrint) {
            for (Peer p : speedsAverage.keySet()) {
                printSpeed(p);
            }
            lastTime3Sec = current;
        }
        if (current - lastTimeSec > sec) {
            for (Peer p : speedsAverage.keySet()) {
                update(p);
            }
            lastTimeSec = current;
        }
    }

    private void update(Peer peer) {
        speedsAverage.put(peer, 0);
    }

    public void addBytes(Peer peer, int bytes) {
        int totalBytes;
        if (speedsAverage.containsKey(peer)) {
            totalBytes = speedsAverage.get(peer) + bytes;
        } else {
            totalBytes = bytes;
        }
        speedsAverage.put(peer, totalBytes);
    }

    private void printSpeed(Peer peer) {
        if (speedsAverage.containsKey(peer)) {
            System.out.println("|| AVG download speed from " + peer + ": " + speedsAverage.get(peer) + " bytes/sec");
        }
    }

    //значения -- кол-во байтов
    private final Map<Peer, Integer> speedsAverage = new HashMap<>(); //считаю все байты отправленные, как секунда проходит -- обнулсяю и ставлю lastTime на current
    private final Map<Peer, Integer> speedsTotal = new HashMap<>(); //считаю все байты отправленные, не обнуляю, считаю время работы в целом, в итоге делю кол-во байтов на время работы
    private long lastTimeSec = 0;
    private long lastTime3Sec = 0;
    private final long timeToPrint = 3000; //millisec
    private final long sec = 1000; //millisec
}
