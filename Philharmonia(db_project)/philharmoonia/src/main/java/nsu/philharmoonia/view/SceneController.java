package nsu.philharmoonia.view;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {
    private Stage primaryStage;
    private final SpringFXMLLoader loader;

    // open/close principle is broken :/

    public SceneController(SpringFXMLLoader loader, Stage stage) {
        this.loader = loader;
        this.primaryStage = stage;
    }

    public void switchScene(FxmlView view) {
        Parent root = load(view.getFxmlFile());
        show(root, view.getTitle());
    }

    private void show(Parent root, String title) {
        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(root);
        }
        scene.setRoot(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
//        primaryStage.sizeToScene();
//        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private Parent load(String fxmlFileName) {
        try {
            return loader.load(fxmlFileName);
        } catch (IOException e) {
            System.err.println("Couldn't load fxml view: " + fxmlFileName);
            Platform.exit();
            return null;
        }
    }


//    private void switchToScene(ActionEvent event, String fxml) throws IOException {
//        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
//        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//        stage.setScene(new Scene(root));
//        stage.show();
//    }
//
//    public void switchToScene1(ActionEvent event) throws IOException {
//        switchToScene(event, "/fxml/scene1.fxml");
//    }
//
//    public void switchToScene2(ActionEvent event) throws IOException {
//        switchToScene(event, "/fxml/scene2.fxml");
//    }
}
