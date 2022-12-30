package nsu.fit.ooad.klavatype.core;

import java.util.HashMap;
import java.util.Map;

public class HighScores implements InformationConsumer, HighScoresInfoStorage {
    @Override
    public Map<Integer, PlayerScorePair> getHighLevelsScores(Mode mode) {
        if (highLevelsScores.containsKey(mode)) {
            return highLevelsScores.get(mode);
        }
        return null; //? maybe throw an exception
    }

    @Override
    public Map<String, Integer> getHighTotalScores(Mode mode) {
        if (highTotalScores.containsKey(mode)) {
            return highTotalScores.get(mode);
        }
        return null;
    }

    @Override
    public void accept(Mode mode, String player, int level, int score) {
        if (!highLevelsScores.containsKey(mode)) {
            highLevelsScores.put(mode, new HashMap<>());
        }
        Map<Integer, PlayerScorePair> levels = highLevelsScores.get(mode);
        levels.put(level, new PlayerScorePair(player, score));

        if (!highTotalScores.containsKey(mode)) {
            highTotalScores.put(mode, new HashMap<>());
        }
    }
    //todo how to update total scores?

    private final Map<Mode, Map<Integer, PlayerScorePair>> highLevelsScores = new HashMap<>();
    private final Map<Mode, Map<String, Integer>> highTotalScores = new HashMap<>();
}
