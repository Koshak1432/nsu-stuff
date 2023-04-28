package kosh.snake;

import javafx.application.Application;
import javafx.stage.Stage;
import kosh.snake.view.MainMenuWindow;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            MainMenuWindow view = new MainMenuWindow();
            primaryStage = MainMenuWindow.getMenuStage();
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
