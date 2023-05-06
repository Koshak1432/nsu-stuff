package nsu.philharmoonia.view;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StageManager {
    private Stage primaryStage;
    private final SpringFXMLLoader loader;

    public StageManager(SpringFXMLLoader loader, Stage stage) {
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
}
