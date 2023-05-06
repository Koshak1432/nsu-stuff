package nsu.philharmoonia.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import nsu.philharmoonia.services.AuthenticationService;
import nsu.philharmoonia.view.FxmlView;
import nsu.philharmoonia.view.StageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class LoginController {
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label statusLabel;

    private final StageManager stageManager;
    private final AuthenticationService authenticationService;

    @Autowired
    @Lazy
    public LoginController(StageManager stageManager, AuthenticationService authenticationService) {
        this.stageManager = stageManager;
        this.authenticationService = authenticationService;
    }

    @FXML
    public void loginButtonPressed(ActionEvent event) {
        Authentication proposedAuthenticationToken = createToken(getLogin(), getPassword());
        try {
            authenticationService.authenticate(proposedAuthenticationToken);
            stageManager.switchScene(FxmlView.MAIN);
        } catch (AuthenticationException e) {
            statusLabel.setText("Invalid login and/or password");
            clearFields();
        }
    }

    private void clearFields() {
        loginField.clear();
        passwordField.clear();
    }

    private Authentication createToken(String login, String password) {
        return new UsernamePasswordAuthenticationToken(login, password);
    }

    public String getLogin() {
        return loginField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }
}
