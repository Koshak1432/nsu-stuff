package nsu.fit.ooad.klavatype.core;

import java.util.Map;

public interface HighScoresInfoStorage {
    Map<Integer, PlayerScorePair> getHighLevelsScores(Mode mode);
    Map<String, Integer> getHighTotalScores(Mode mode);
}
