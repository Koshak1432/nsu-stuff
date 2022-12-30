package com.example.fothlab;

import java.util.List;
import java.util.Map;

public interface NotifiableClient {
    void notifyOnGamesListChange(List<GameAndMasterAddress> games);
    void notifyOnGameStart();
    void notifyOnStateChange(int fieldWidth, int fieldHeight,
                            SnakeProto.GameState state, Map<Integer,
                            List<SnakeProto.GameState.Coord>> snakesFullCoords);

    void notifyOnError(String error);
}
