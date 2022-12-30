package nsu.fit.ooad.klavatype.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nsu.fit.ooad.klavatype.core.KlavaTypeCoreLogic;
import nsu.fit.ooad.klavatype.core.Progress;
import nsu.fit.ooad.klavatype.core.ProgressListener;

import java.util.Objects;

public class KlavatypeGui extends Application implements ProgressListener {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        core = new KlavaTypeCoreLogic(this);
        FXMLLoader mainSceneLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().
                getResource("mainScene.fxml")));
        Parent mainScenePane = mainSceneLoader.load();
        mainSceneController = mainSceneLoader.getController();
        Scene mainScene = new Scene(mainScenePane);

        FXMLLoader gameChoiceSceneLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().
                getResource("levelChoiceScene.fxml")));
        Parent gameChoiceScenePane = gameChoiceSceneLoader.load();
        levelChoiceSceneController = gameChoiceSceneLoader.getController();
        Scene levelChoiceScene = new Scene(gameChoiceScenePane);

        FXMLLoader highScoresSceneLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().
                getResource("highScoresScene.fxml")));
        Parent highScoresScenePane = highScoresSceneLoader.load();
        highScoresSceneController = highScoresSceneLoader.getController();
        Scene highScoresScene = new Scene(highScoresScenePane);

        FXMLLoader gameSceneLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().
                getResource("gameScene.fxml")));
        Parent gameScenePane = gameSceneLoader.load();
        gameSceneController = gameSceneLoader.getController();
        Scene gameScene = new Scene(gameScenePane);

        mainSceneController.setLevelChoiceScene(levelChoiceScene);
        mainSceneController.setLevelChoiceSceneController(levelChoiceSceneController);
        mainSceneController.setHighScoresScene(highScoresScene);
        mainSceneController.setHighScoresSceneController(highScoresSceneController);
        mainSceneController.setCore(core);
        highScoresSceneController.setMainScene(mainScene);
        highScoresSceneController.setCore(core);
        levelChoiceSceneController.setMainScene(mainScene);
        levelChoiceSceneController.setMainSceneController(mainSceneController);
        levelChoiceSceneController.setGameScene(gameScene);
        levelChoiceSceneController.setGameSceneController(gameSceneController);
        levelChoiceSceneController.setCore(core);
        gameSceneController.setMainScene(mainScene);
        gameSceneController.setMainSceneController(mainSceneController);
        gameSceneController.setCore(core);

        stage.setTitle("KlavaType");
        stage.setScene(mainScene);
        stage.show();
    }

    @Override
    public void notifyProgress(Progress progress) {

    }

    private KlavaTypeCoreLogic core;
    private MainSceneController mainSceneController;
    private LevelChoiceSceneController levelChoiceSceneController;
    private GameSceneController gameSceneController;
    private HighScoresSceneController highScoresSceneController;
}
