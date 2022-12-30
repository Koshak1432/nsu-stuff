package nsu.fit.ooad.klavatype.core;

public class SessionInfo implements SessionInfoStorage, InformationConsumer {
    public SessionInfo(Mode mode, int level, String player) {
        this.mode = mode;
        this.level = level;
        this.player = player;
    }

    // maybe update only score
    @Override
    public void accept(Mode mode, String player, int level, int score) {
        this.mode = mode;
        this.player = player;
        this.level = level;
        this.score = score;
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public int getScore() {
        return score;
    }

    private Mode mode;
    private int level;
    private String player;
    private int score;
}
