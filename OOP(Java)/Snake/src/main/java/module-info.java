module kosh.snake {
    requires javafx.controls;
    requires javafx.fxml;


    opens kosh.snake to javafx.fxml;
    exports kosh.snake;
    exports kosh.snake.model;
    opens kosh.snake.model to javafx.fxml;
    exports kosh.snake.view;
    opens kosh.snake.view to javafx.fxml;
    exports kosh.snake.controller;
    opens kosh.snake.controller to javafx.fxml;
}