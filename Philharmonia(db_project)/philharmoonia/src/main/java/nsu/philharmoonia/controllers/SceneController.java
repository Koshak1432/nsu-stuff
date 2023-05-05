package nsu.philharmoonia.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class SceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    // open/close principle is broken :/
    private void switchToScene(ActionEvent event, String fxml) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void switchToScene1(ActionEvent event) throws IOException {
        switchToScene(event, "/fxml/scene1.fxml");
    }

    public void switchToScene2(ActionEvent event) throws IOException {
        switchToScene(event, "/fxml/scene2.fxml");
    }
}
