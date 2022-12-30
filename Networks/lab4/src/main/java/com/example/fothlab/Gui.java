package com.example.fothlab;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Gui extends Application implements NotifiableClient {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        game = new SnakeLogic(this);
        FXMLLoader mainSceneLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().
                getResource("mainScene.fxml")));
        Parent mainScenePane = mainSceneLoader.load();
        mainController = mainSceneLoader.getController();
        Scene mainScene = new Scene(mainScenePane);

        FXMLLoader gameChoiceSceneLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().
                getResource("gameChoiceScene.fxml")));
        Parent gameChoiceScenePane = gameChoiceSceneLoader.load();
        choiceController = gameChoiceSceneLoader.getController();
        Scene gameChoiceScene = new Scene(gameChoiceScenePane);

        FXMLLoader gameSceneLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().
                getResource("gameScene.fxml")));
        Parent gameScenePane = gameSceneLoader.load();
        gameController = gameSceneLoader.getController();
        Scene gameScene = new Scene(gameScenePane);

        mainController.setGameChoiceScene(gameChoiceScene);
        mainController.setGameChoiceSceneController(choiceController);
        mainController.setGameScene(gameScene);
        mainController.setGameSceneController(gameController);
        mainController.setGame(game);
        choiceController.setMainScene(mainScene);
        choiceController.setMainSceneController(mainController);
        choiceController.setGameScene(gameScene);
        choiceController.setGameSceneController(gameController);
        choiceController.setGame(game);
        choiceController.initListeners();
        gameController.setMainScene(mainScene);
        gameController.setMainSceneController(mainController);
        gameController.setGame(game);

        stage.setTitle("Snake");
        stage.setScene(mainScene);
        stage.show();
    }

    @Override
    public void notifyOnGamesListChange(List<GameAndMasterAddress> games) {
        Platform.runLater(() -> choiceController.notifyOnGamesListChange(games));
    }

    @Override
    public void notifyOnGameStart() {
        Platform.runLater(() -> gameController.notifyOnGameStart());
    }

    @Override
    public void notifyOnStateChange(int fieldWidth, int fieldHeight,
                                    SnakeProto.GameState state,
                                    Map<Integer, List<SnakeProto.GameState.Coord>> snakesFullCoords) {
        Platform.runLater(() -> gameController.notifyOnStateChange(fieldWidth, fieldHeight,
                                                                   state, snakesFullCoords));
    }

    @Override
    public void notifyOnError(String error) {
        Platform.runLater(() -> showError(error));
    }

    private void showError(String error) {
        try {
            FXMLLoader errorNotificationSceneLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().
                    getResource("errorNotificationScene.fxml")));
            Parent errorScenePane = errorNotificationSceneLoader.load();
            ErrorController errorSceneController = errorNotificationSceneLoader.getController();
            Scene errorScene = new Scene(errorScenePane);
            Stage stage = new Stage();
            stage.setTitle("Error");
            stage.setScene(errorScene);
            errorSceneController.setLabelText(error);
            stage.show();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fatal error loading fxml");
        }
    }

    private SnakeLogic game;
    private MainController mainController;
    private ChoiceController choiceController;
    private GameController gameController;

}