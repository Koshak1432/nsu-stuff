package nsu.fit.ooad.klavatype.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nsu.fit.ooad.klavatype.core.KlavaTypeCoreLogic;

public class HighScoresSceneController {
    public ListView<String> listView;
    public Button backButton;

    public void onBackButtonPressed(MouseEvent mouseEvent) {
        ((Stage)(backButton.getScene().getWindow())).setScene(mainScene);
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void setCore(KlavaTypeCoreLogic core) {
        this.core = core;
    }

    private KlavaTypeCoreLogic core;
    private Scene mainScene;
}
