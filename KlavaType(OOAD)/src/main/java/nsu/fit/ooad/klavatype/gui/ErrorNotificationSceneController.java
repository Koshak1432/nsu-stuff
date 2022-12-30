package nsu.fit.ooad.klavatype.gui;

import javafx.scene.control.Label;

public class ErrorNotificationSceneController {
    public Label errorDescLabel;

    public void setLabelText(String text) {
        errorDescLabel.setText(text);
    }
}
