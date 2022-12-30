package nsu.fit.ooad.klavatype.core;

import java.util.Map;

public class ProgressInfo implements InformationConsumer, ProgressInfoStorage {
    public ProgressInfo(String player) {
        this.player = player;
    }

    //?? Player(String name) in uml

    @Override
    public void accept(Mode mode, String player, int level, int score) {

    }

    @Override
    public int getMaxLevel(Mode mode) {
        if (maxLevels.containsKey(mode)) {
            return maxLevels.get(mode);
        }
        return 0; // ?
    }

    private final String player;
    private Map<Mode, Integer> maxLevels;
}
