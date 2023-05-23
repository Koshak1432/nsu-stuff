package nsu.philharmoonia;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import nsu.philharmoonia.view.FxmlView;
import nsu.philharmoonia.view.StageManager;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PhilharmooniaApplication extends Application {
    private ConfigurableApplicationContext context;
    private StageManager controller;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = context.getBean(StageManager.class, primaryStage);
//        controller.switchScene(FxmlView.LOGIN);
        controller.switchScene(FxmlView.MAIN);
    }

    public void init() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(
                PhilharmooniaApplication.class);
        builder.application().setWebApplicationType(WebApplicationType.NONE);
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
