package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.ArtistDTO;
import nsu.philharmonia.model.dto.ArtistToGenreDTO;
import nsu.philharmonia.model.dto.GenreDTO;
import nsu.philharmonia.model.entities.Artist;
import nsu.philharmonia.model.entities.Genre;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.model.exceptions.NotFoundException;
import nsu.philharmonia.repositories.ArtistRepository;
import nsu.philharmonia.repositories.GenreRepository;
import nsu.philharmonia.services.ArtistService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ArtistServiceImpl implements ArtistService {
    private final ModelMapper mapper;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository, GenreRepository genreRepository, ModelMapper mapper) {
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<Void> saveArtistToGenre(ArtistToGenreDTO dto) throws InvalidInputException {
        Artist a = artistRepository.findArtistByNameAndSurname(dto.getArtist().getName(), dto.getArtist().getSurname()).orElseThrow(() -> new InvalidInputException("Invalid aritst"));
        Genre g = genreRepository.findByName(dto.getGenre().getName()).orElseThrow(() -> new InvalidInputException("Invalid genre"));
        Set<Genre> genres = a.getGenres();
        Set<Artist> artists = g.getArtists();
        artists.add(a);
        genres.add(g);
        g.setArtists(artists);
        a.setGenres(genres);
        genreRepository.save(g);
        artistRepository.save(a);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<List<ArtistToGenreDTO>> getArtistToGenres() {
        List<Artist> artists = (List<Artist>) artistRepository.findAll();
        List<ArtistToGenreDTO> dtos = new ArrayList<>();
        for (Artist a : artists) {
            for (Genre g : a.getGenres()) {
                ArtistToGenreDTO dto = new ArtistToGenreDTO();
                dto.setArtist(mapper.map(a, ArtistDTO.class));
                dto.setGenre(mapper.map(g, GenreDTO.class));
                dtos.add(dto);
            }
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ArtistDTO>> getAll() {
        List<Artist> artists = (List<Artist>) artistRepository.findAll();
        List<ArtistDTO> artistDTOS = artists.stream().map(artist -> mapper.map(artist, ArtistDTO.class)).toList();
        return new ResponseEntity<>(artistDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ArtistDTO> getArtistById(Long id) throws NotFoundException {
        Artist artist = findArtist(id);
        ArtistDTO artistDTO = mapper.map(artist, ArtistDTO.class);
        return new ResponseEntity<>(artistDTO, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<ArtistDTO> addArtist(ArtistDTO artistDTO) {
        Artist artist = mapper.map(artistDTO, Artist.class);
        artistRepository.save(artist);
        return new ResponseEntity<>(artistDTO, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> updateArtist(ArtistDTO artistDTO) throws NotFoundException {
        Artist artist = artistRepository.findById(artistDTO.getId()).orElseThrow(
                () -> new NotFoundException("Not found artist to update"));
        System.out.println(
                "updating artist: name: " + artist.getName() + "\nsurname: " + artist.getSurname() + "\ngenres: " + artist.getGenres()
                        + "\nimpresarios: " + artist.getImpresarios() + "\nperformances: " + artist.getPerformances()
                        + "\ncontestPlaces: " + artist.getContestPlaces());
        artist.setName(artistDTO.getName());
        artist.setSurname(artistDTO.getSurname());
        //todo update properly other
        artistRepository.save(artist);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteArtist(Long id) {
        artistRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Artist findArtist(Long id) throws NotFoundException {
        Optional<Artist> artistOptional = artistRepository.findById(id);
        if (artistOptional.isEmpty()) {
            throw new NotFoundException("Not found artist");
        }
        return artistOptional.get();
    }
}
