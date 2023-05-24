package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.ArtistDTO;
import nsu.philharmonia.model.entities.Artist;
import nsu.philharmonia.model.exceptions.NotFoundException;
import nsu.philharmonia.repositories.ArtistRepository;
import nsu.philharmonia.services.ArtistService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtistServiceImpl implements ArtistService {
    private final ModelMapper mapper;
    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository, ModelMapper mapper) {
        this.artistRepository = artistRepository;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<List<ArtistDTO>> getAll() {
        List<Artist> artists = (List<Artist>) artistRepository.findAll();
        List<ArtistDTO> artistDTOS = artists.stream()
                .map(artist -> mapper.map(artist, ArtistDTO.class))
                .toList();
        return new ResponseEntity<>(artistDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ArtistDTO> getArtistById(Long id) throws NotFoundException {
        Artist artist = findArtist(id);
        ArtistDTO artistDTO = mapper.map(artist, ArtistDTO.class);
        return new ResponseEntity<>(artistDTO, HttpStatus.OK);
    }

    private Artist findArtist(Long id) throws NotFoundException {
        Optional<Artist> artistOptional = artistRepository.findById(id);
        if (artistOptional.isEmpty()) {
            throw new NotFoundException("Not found artist");
        }
        return artistOptional.get();
    }
}
