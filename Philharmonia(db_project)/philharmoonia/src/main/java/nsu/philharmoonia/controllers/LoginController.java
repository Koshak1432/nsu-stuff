package nsu.philharmoonia.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import nsu.philharmoonia.services.AuthenticationService;
import nsu.philharmoonia.view.StageManager;
import org.springframework.stereotype.Component;

@Component
public class LoginController {
    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;


    private final StageManager stageManager;

    private final AuthenticationService authenticationService;

}
