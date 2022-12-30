package com.example.fothlab;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainController {
    public Label mainSceneTitle;
    public Button newGameButton;
    public Button joinGameButton;
    public Button joinGameAsViewerButton;

    public void onNewGameButtonPressed(MouseEvent mouseEvent) {
        ((Stage)(newGameButton.getScene().getWindow())).setScene(gameScene);
        Thread gamesThread = new Thread(game::newGame);
        gameController.setBackgroundWorkingThread(gamesThread);
        gamesThread.start();
    }

    public void onJoinGameButtonPressed(MouseEvent mouseEvent) {
        choiceController.setJoiningRole(SnakeProto.NodeRole.NORMAL);
        ((Stage)(joinGameButton.getScene().getWindow())).setScene(gameChoiceScene);
        Thread gamesSearchThread = new Thread(game::searchForAvailableGames);
        choiceController.setBackgroundWorkingThread(gamesSearchThread);
        gamesSearchThread.start();
    }

    public void onJoinGameAsViewerButtonPressed(MouseEvent mouseEvent) {
        choiceController.setJoiningRole(SnakeProto.NodeRole.VIEWER);
        ((Stage)(joinGameButton.getScene().getWindow())).setScene(gameChoiceScene);
        Thread gamesSearchThread = new Thread(game::searchForAvailableGames);
        choiceController.setBackgroundWorkingThread(gamesSearchThread);
        gamesSearchThread.start();
    }

    public void setGameScene(Scene gameScene) {
        this.gameScene = gameScene;
    }

    public void setGameSceneController(GameController gameController) {
        this.gameController = gameController;
    }

    public void setGameChoiceScene(Scene gameChoiceScene) {
        this.gameChoiceScene = gameChoiceScene;
    }

    public void setGameChoiceSceneController(ChoiceController choiceController) {
        this.choiceController = choiceController;
    }

    public void setGame(SnakeLogic game) {
        this.game = game;
    }

    private SnakeLogic game;
    private Scene gameScene;
    private GameController gameController;
    private Scene gameChoiceScene;
    private ChoiceController choiceController;
}