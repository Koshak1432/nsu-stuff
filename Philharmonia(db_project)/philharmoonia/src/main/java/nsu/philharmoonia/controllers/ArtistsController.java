package nsu.philharmoonia.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

@Component
public class ArtistsController {

    @FXML
    private Button addEntryButton;

    @FXML
    private DatePicker dateSince;

    @FXML
    private DatePicker dateTo;

    @FXML
    private Button deleteEntryButton;

    @FXML
    private Button findByGenreButton;

    @FXML
    private Button findByImpresarioButton;

    @FXML
    private Button findMultigenreButton;

    @FXML
    private Button findNotParticipatingButton;

    @FXML
    private Button findWinnersButton;

    @FXML
    private Button listAllButton;

    @FXML
    private TableView<?> table;

    @FXML
    private Button updateEntryButton;

}
