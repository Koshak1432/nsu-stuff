package nsu.philharmoonia.controllers.dialogs;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.synedra.validatorfx.TooltipWrapper;
import nsu.philharmoonia.model.entities.Artist;
import net.synedra.validatorfx.Validator;

import java.util.Map;

public class AddArtistController {
    private Artist artist = new Artist();
    @FXML
    private DialogPane dialogPane;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    private final Validator validator = new Validator();
    TooltipWrapper<Button> okWrapper;

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
                                         Bindings.concat("Cannot save artist:\n",
                                                         validator.createStringBinding()));
    }


    public void setArtist(Artist artist) {

    }

    public Artist getArtist() {
        artist.setName(nameField.getText());
        artist.setSurname(surnameField.getText());
        return artist;
    }
}
