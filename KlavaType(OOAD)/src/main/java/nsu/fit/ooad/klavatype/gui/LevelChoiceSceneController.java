package nsu.fit.ooad.klavatype.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nsu.fit.ooad.klavatype.core.KlavaTypeCoreLogic;

public class LevelChoiceSceneController {
    public Button level1Button;
    public Button level2Button;
    public Button level3Button;
    public Button backButton;

    public void backButtonOnLevelChoiceScenePressed(MouseEvent mouseEvent) {
        ((Stage)(backButton.getScene().getWindow())).setScene(mainScene);
    }

    public void setGameScene(Scene gameScene) {
        this.gameScene = gameScene;
    }

    public void setGameSceneController(GameSceneController gameSceneController) {
        this.gameSceneController = gameSceneController;
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void setMainSceneController(MainSceneController mainSceneController) {
        this.mainSceneController = mainSceneController;
    }

    public void setCore(KlavaTypeCoreLogic core) {
        this.core = core;
    }

    public void onLevel1ButtonPressed(MouseEvent mouseEvent) {
    }

    public void onLevel2ButtonPressed(MouseEvent mouseEvent) {
    }

    public void onLevel3ButtonPressed(MouseEvent mouseEvent) {
    }

    private KlavaTypeCoreLogic core;
    private Scene mainScene;
    private MainSceneController mainSceneController;
    private Scene gameScene;
    private GameSceneController gameSceneController;
}
