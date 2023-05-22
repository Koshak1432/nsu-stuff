package nsu.philharmoonia.services.impl;

import nsu.philharmoonia.model.entities.Artist;
import nsu.philharmoonia.repositories.ArtistRepository;
import nsu.philharmoonia.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }
    @Override
    public List<Artist> getAll() {
        return (List<Artist>) artistRepository.findAll();
    }

    @Override
    public void saveArtist(Artist artist) {
        //here is validation

        artistRepository.save(artist);
    }
}
