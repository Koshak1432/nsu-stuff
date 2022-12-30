package nsu.fit.ooad.klavatype.core;

public class KlavaTypeCoreLogic implements CoreLogic {
    public KlavaTypeCoreLogic(ProgressListener progressListener) {

    }
    @Override
    public void play(Mode mode, int level, String player) {
        sessionLogic.createSession(mode, level, player);
        // and game logic
    }

    @Override
    public void notifyKeyPressed(Character symbol) {

    }

    private final SessionLogic sessionLogic = new SessionLogic();
}
