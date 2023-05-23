package nsu.philharmoonia.controllers.dialogs;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import nsu.philharmoonia.model.entities.Artist;
import nsu.philharmoonia.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddArtistController {
    private Artist artist = new Artist();
    @FXML
    private DialogPane dialogPane;
    @FXML
    private TextField id;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private ComboBox<Long> ids;
    private final Validator validator = new Validator();
    private TooltipWrapper<Button> okWrapper;

    private final ArtistService artistService;

    @Autowired
    public AddArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @FXML
    public void initialize() {
        validator.createCheck().dependsOn("name", nameField.textProperty()).dependsOn("surname",
                                                                                      surnameField.textProperty()).withMethod(
                c -> {
                    String name = c.get("name");
                    String surname = c.get("surname");
                    if (! name.matches("[a-zA-Z]+") || ! surname.matches("[a-zA-Z]+")) {
                        c.error("Please use only letters");
                    }
                }).decorates(nameField).decorates(surnameField).immediate();

        okWrapper = new TooltipWrapper<>((Button) dialogPane.lookupButton(ButtonType.OK),
                                         validator.containsErrorsProperty(),
                                         Bindings.concat("Cannot save artist:\n", validator.createStringBinding()));

        List<Artist> artists = artistService.getAll();
        ObservableList<Long> artistIds = FXCollections.observableList(artists.stream().map(Artist::getId).toList());
        ids.setItems(artistIds);
    }


    public void setArtist(Artist artist) {

    }

    public Artist getArtist() {
        artist.setName(nameField.getText());
        artist.setSurname(surnameField.getText());
        return artist;
    }
}
