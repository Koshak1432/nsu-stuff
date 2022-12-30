package com.example.fothlab;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoiceController {
    public ListView<String> gamesListView;
    public Button backButton;

    public void backButtonOnGamesChoiceScenePressed(MouseEvent mouseEvent) {
        backgroundWorkingThread.interrupt();
        ((Stage)(backButton.getScene().getWindow())).setScene(mainScene);
    }

    public void setGameScene(Scene gameScene) {
        this.gameScene = gameScene;
    }

    public void setGameSceneController(GameController gameController) {
        this.gameController = gameController;
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void setMainSceneController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initListeners() {
        gamesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String selected = gamesListView.getSelectionModel().getSelectedItem();
                GameAndMasterAddress selectedGame = gamesDataMap.get(selected);
                if (selectedGame == null) {
                    return;
                }
                ((Stage)(gamesListView.getScene().getWindow())).setScene(gameScene);
                backgroundWorkingThread.interrupt();
                Thread gameThread = new Thread(() -> game.joinGame(selectedGame, joiningRole));
                gameController.setBackgroundWorkingThread(gameThread);
                gameThread.start();
            }
        });
    }

    public void notifyOnGamesListChange(List<GameAndMasterAddress> games) {
        gamesDataMap.clear();
        gamesListView.getItems().clear();
        for (GameAndMasterAddress gameAddressPair : games) {
            String gameAnnounceString = gameAddressPair.game().getGameName() + "\n\t"
                    + gameAddressPair.address().toString() + "\n\t"
                    + gameAddressPair.game().getPlayers().getPlayersCount() + " players";
            gamesListView.getItems().add(gameAnnounceString);
            gamesDataMap.put(gameAnnounceString, gameAddressPair);
        }
    }

    public void setGame(SnakeLogic game) {
        this.game = game;
    }

    public void setBackgroundWorkingThread(Thread backgroundWorkingThread) {
        this.backgroundWorkingThread = backgroundWorkingThread;
    }

    public void setJoiningRole(SnakeProto.NodeRole joiningRole) {
        this.joiningRole = joiningRole;
    }

    private SnakeProto.NodeRole joiningRole;
    private Map<String, GameAndMasterAddress> gamesDataMap = new HashMap<>();
    private Thread backgroundWorkingThread;
    private SnakeLogic game;
    private Scene mainScene;
    private MainController mainController;
    private Scene gameScene;
    private GameController gameController;
}
