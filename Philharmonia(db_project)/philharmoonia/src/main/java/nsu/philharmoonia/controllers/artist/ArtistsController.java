package nsu.philharmoonia.controllers.artist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nsu.philharmoonia.controllers.dialogs.AddArtistController;
import nsu.philharmoonia.model.entities.Artist;
import nsu.philharmoonia.services.ArtistService;
import nsu.philharmoonia.view.StageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.Optional;
import com.dlsc.formsfx.model.structure.Form;

import java.io.IOException;
import java.util.List;

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
    private TableView<Artist> table;

    @FXML
    private TableColumn<Artist, Long> idCol;

    @FXML
    private TableColumn<Artist, String> nameCol;

    @FXML
    private TableColumn<Artist, String> surnameCol;

    @FXML
    private Button updateEntryButton;

    private StageManager stageManager;

    private ArtistService artistService;


    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
    }


    @Autowired
    @Lazy
    public ArtistsController(StageManager stageManager, ArtistService artistService) {
        this.artistService = artistService;
        this.stageManager = stageManager;

    }

    @FXML
    public void listAllEntries() {
        List<Artist> data = artistService.getAll();
        ObservableList<Artist> observableList = FXCollections.observableList(data);
        table.setItems(observableList);
    }

    private DialogPane getPane(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            System.err.println("COULDN'T load add artist dialog");
            e.printStackTrace();
            return null;
        }
    }

    Optional<ButtonType> showDialog(DialogPane dialogPane) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle("Add artist title");
        dialog.initModality(Modality.APPLICATION_MODAL);
        return dialog.showAndWait();
    }

    @FXML
    public void addArtist() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/addArtistDialog.fxml"));
        DialogPane dialogPane = getPane(loader);
        if (dialogPane == null) {
            return;
        }
        Optional<ButtonType> clickedButton = showDialog(dialogPane);
        AddArtistController controller = loader.getController();
        if (clickedButton.isPresent()) {
            if (clickedButton.get() == ButtonType.OK) {
                // todo validation in service??
                System.out.println("adding artist");
                Artist artistFromDialog = controller.getArtist();
                artistService.saveArtist(artistFromDialog);
            }
        }
    }

    @FXML
    public void updateArtist() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/updateArtistDialog.fxml"));


    }

    @FXML
    public void deleteArtist() {

    }


}
