package nsu.fit.ooad.klavatype.core;

import java.util.ArrayList;
import java.util.List;

public class SessionLogic {
    public void createSession(Mode mode, int level, String player) {
        informationConsumers.add(new ProgressInfo(player));
        informationConsumers.add(new SessionInfo(mode, level, player));
        informationConsumers.add(new HighScores());
    }

    public void update(int score) {

    }



    private final List<InformationConsumer> informationConsumers = new ArrayList<>();
}
