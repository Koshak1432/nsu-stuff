package nsu.fit.ooad.klavatype.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nsu.fit.ooad.klavatype.core.KlavaTypeCoreLogic;

public class MainSceneController {
    public Button chooseLevelButton;
    public Button highScoresButton;

    public void onChooseLevelButtonPressed(MouseEvent mouseEvent) {
        ((Stage)(chooseLevelButton.getScene().getWindow())).setScene(levelChoiceScene);
    }

    public void onHighScoresButtonPressed(MouseEvent mouseEvent) {
        ((Stage)(highScoresButton.getScene().getWindow())).setScene(highScoresScene);
    }

    public void setHighScoresScene(Scene highScoresScene) {
        this.highScoresScene = highScoresScene;
    }

    public void setHighScoresSceneController(HighScoresSceneController highScoresSceneController) {
        this.highScoresSceneController = highScoresSceneController;
    }

    public void setLevelChoiceScene(Scene levelChoiceScene) {
        this.levelChoiceScene = levelChoiceScene;
    }

    public void setLevelChoiceSceneController(LevelChoiceSceneController levelChoiceSceneController) {
        this.levelChoiceSceneController = levelChoiceSceneController;
    }

    public void setCore(KlavaTypeCoreLogic core) {
        this.core = core;
    }

    private KlavaTypeCoreLogic core;
    private Scene highScoresScene;
    private HighScoresSceneController highScoresSceneController;
    private Scene levelChoiceScene;
    private LevelChoiceSceneController levelChoiceSceneController;
}