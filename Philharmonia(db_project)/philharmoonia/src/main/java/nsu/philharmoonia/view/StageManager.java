package nsu.philharmoonia.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import nsu.philharmoonia.SpringFXMLLoader;

import java.io.IOException;
import java.util.Objects;

public class StageManager {
    public void switchScene(Parent parent, String fxml) throws IOException {
        Parent nextParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));

    }
}
