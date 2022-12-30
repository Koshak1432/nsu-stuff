package com.example.fothlab;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class GameController {
    public Canvas gameCanvas;
    public ListView<String> playersListView;
    public TextField addressTextField;
    public TextField portTextField;
    public Button inviteButton;
    public Button exitButton;

    public void onKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case UP, W -> {
                game.putDirectionCommand(SnakeProto.Direction.UP);
            }
            case DOWN, S-> {
                game.putDirectionCommand(SnakeProto.Direction.DOWN);
            }
            case LEFT, A -> {
                game.putDirectionCommand(SnakeProto.Direction.LEFT);
            }
            case RIGHT, D -> {
                game.putDirectionCommand(SnakeProto.Direction.RIGHT);
            }
        }
    }

    public void onInviteButtonPressed() {
        InetSocketAddress address = new InetSocketAddress(addressTextField.getText(),
                                        Integer.parseInt(portTextField.getText()));
        if (address.isUnresolved()) {
            return;
        }
        game.invite(address);
    }

    public void onExitButtonPressed() {
        backgroundWorkingThread.interrupt();
        ((Stage)(exitButton.getScene().getWindow())).setScene(mainScene);
    }

    public void notifyOnGameStart() {
        idsToColorsMap.clear();
        double width = gameCanvas.getWidth();
        double height = gameCanvas.getHeight();
        GraphicsContext graphicsContext = gameCanvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, width, height);
        playersListView.getItems().clear();
//        addressTextField.setText("snakes.ippolitov.me");
//        portTextField.setText("9192");
    }

    public void notifyOnStateChange(int fieldWidth, int fieldHeight,
                                    SnakeProto.GameState state,
                                    Map<Integer, List<SnakeProto.GameState.Coord>> snakesFullCoords) {
        playersListView.getItems().clear();
        double width = gameCanvas.getWidth();
        double height = gameCanvas.getHeight();
        double cellWidth = width / (double) fieldWidth;
        double cellHeight = height / (double) fieldHeight;
        double minCellDimension = Math.min(cellHeight, cellWidth);
        GraphicsContext graphicsContext = gameCanvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, width, height);
        double aliveCellBorderWidth = 1.0;
        double deadCellBorderWidth = 3.0;
        List<SnakeProto.GameState.Snake> snakes = state.getSnakesList();
        for (SnakeProto.GameState.Snake snake : snakes) {
            int playerId = snake.getPlayerId();
            double borderWidth = (snake.getState() == SnakeProto.GameState.Snake.SnakeState.ALIVE) ?
                    aliveCellBorderWidth : deadCellBorderWidth;
            Color curSnakeColor = idsToColorsMap.get(playerId);
            if (curSnakeColor == null) {
                curSnakeColor = Color.color(Math.random(), Math.random(), Math.random());
                if (curSnakeColor.getBrightness() < 0.3) {
                    curSnakeColor = curSnakeColor.brighter();
                }
                idsToColorsMap.put(playerId, curSnakeColor);
            }
            graphicsContext.setFill(curSnakeColor);
            List<SnakeProto.GameState.Coord> fullPointsList = snakesFullCoords.get(playerId);
            ListIterator<SnakeProto.GameState.Coord> pointsIterator = fullPointsList.listIterator();
            SnakeProto.GameState.Coord head = pointsIterator.next();
            graphicsContext.fillRect(cellWidth * head.getX() + borderWidth - 1.0,
                                    cellHeight * head.getY() + borderWidth - 1.0,
                                    minCellDimension - 2 * (borderWidth - 1.0),
                                    minCellDimension - 2 * (borderWidth - 1.0));
            while (pointsIterator.hasNext()) {
                SnakeProto.GameState.Coord point = pointsIterator.next();
                graphicsContext.fillRect(cellWidth * point.getX() + borderWidth,
                        cellHeight * point.getY() + borderWidth,
                        minCellDimension - 2 * borderWidth,
                        minCellDimension - 2 * borderWidth);
            }
        }
        double dotWidth = minCellDimension / 2;
        double dotHeight = dotWidth;
        double dotWidthOffset = cellWidth / 2 - dotWidth / 2;
        double dotHeightOffset = cellHeight / 2 - dotHeight / 2;
        List<SnakeProto.GameState.Coord> foods = state.getFoodsList();
        graphicsContext.setFill(Color.WHITE);
        for (SnakeProto.GameState.Coord food : foods) {
            graphicsContext.fillOval(cellWidth * food.getX() + dotWidthOffset,
                    cellHeight * food.getY() + dotHeightOffset,
                    dotWidth, dotHeight);
        }
        List<SnakeProto.GamePlayer> players = state.getPlayers().getPlayersList();
        for (SnakeProto.GamePlayer player : players) {
            String playerDesc = player.getName() + ": " + player.getScore();
            playersListView.getItems().add(playerDesc);
        }
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void setMainSceneController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setGame(SnakeLogic game) {
        this.game = game;
    }

    public void setBackgroundWorkingThread(Thread backgroundWorkingThread) {
        this.backgroundWorkingThread = backgroundWorkingThread;
    }

    private final Map<Integer, Color> idsToColorsMap = new HashMap<>();
    private Thread backgroundWorkingThread;
    private SnakeLogic game;
    private Scene mainScene;
    private MainController mainController;
}
