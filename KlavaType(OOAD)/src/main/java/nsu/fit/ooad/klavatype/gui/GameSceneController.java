package nsu.fit.ooad.klavatype.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import nsu.fit.ooad.klavatype.core.KlavaTypeCoreLogic;

public class GameSceneController {
    public TextArea textArea;
    public Button exitButton;

    public void onKeyPressed(KeyEvent keyEvent) {
        core.notifyKeyPressed(keyEvent.getCode().getChar().charAt(0));
    }

    public void onExitButtonPressed() {
        ((Stage)(exitButton.getScene().getWindow())).setScene(mainScene);
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

    private KlavaTypeCoreLogic core;
    private Scene mainScene;
    private MainSceneController mainSceneController;
}
