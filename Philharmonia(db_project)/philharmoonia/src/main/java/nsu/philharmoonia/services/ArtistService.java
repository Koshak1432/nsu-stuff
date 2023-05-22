package nsu.philharmoonia.services;

import nsu.philharmoonia.model.entities.Artist;
import nsu.philharmoonia.model.entities.Impresario;
import nsu.philharmoonia.repositories.ArtistRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

public interface ArtistService {
    List<Artist> getAll();
    void saveArtist(Artist artist);
}
