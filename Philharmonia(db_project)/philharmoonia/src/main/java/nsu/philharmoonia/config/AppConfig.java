package nsu.philharmoonia.config;

import javafx.stage.Stage;
import nsu.philharmoonia.view.SpringFXMLLoader;
import nsu.philharmoonia.view.StageManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Configuration
public class AppConfig {
    @Autowired
    SpringFXMLLoader loader;

    // todo stange thing might happen
    @Bean
    @Lazy
    @Autowired(required = false)
    public StageManager sceneController(Stage stage) {
        return new StageManager(loader, stage);
    }
}