package nsu.philharmoonia.services;

import nsu.philharmoonia.model.entities.Artist;
import nsu.philharmoonia.model.entities.Impresario;
import org.springframework.stereotype.Service;

import java.util.Collection;

public interface ImpresarioService {
    Collection<Impresario> getArtistImpresarios(Artist artist);
}
