package nsu.philharmoonia;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nsu.philharmoonia.view.FxmlView;
import nsu.philharmoonia.view.SceneController;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PhilharmooniaApplication extends Application {
    private ConfigurableApplicationContext context;
    private SceneController controller;

    public static void main(String[] args) {
//        SpringApplication.run(PhilharmooniaApplication.class, args);
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = context.getBean(SceneController.class, primaryStage);
        controller.switchScene(FxmlView.MAIN);
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
