package nsu.philharmoonia;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PhilharmooniaApplication extends Application {
    private ConfigurableApplicationContext context;

    public static void main(String[] args) {
//        SpringApplication.run(PhilharmooniaApplication.class, args);
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(new StackPane(), 640, 480));
        primaryStage.show();
    }

    public void init() {
        SpringApplication.run(PhilharmooniaApplication.class);

    }


}
