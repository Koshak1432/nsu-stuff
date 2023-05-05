package nsu.philharmoonia;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/scene1.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root, 640, 480));

        primaryStage.show();
    }

    public void init() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(
                PhilharmooniaApplication.class);
//        builder.application().setWebApplicationType(WebApplicationType.NONE);
        builder.headless(false);
        String[] args = getParameters().getRaw().toArray(String[]::new);
        context = builder.run(args);
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }


}
