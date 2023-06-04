package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.GenreDTO;
import nsu.philharmonia.model.entities.Genre;
import nsu.philharmonia.repositories.GenreRepository;
import nsu.philharmonia.services.GenreService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final ModelMapper mapper;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository, ModelMapper mapper) {
        this.genreRepository = genreRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteGenre(Long id) {
        genreRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<GenreDTO> saveGenre(GenreDTO genre) {
        Genre saved = genreRepository.findById(genre.getId()).map(g -> {
            g.setName(genre.getName());
            return genreRepository.save(g);
        }).orElseGet(() -> genreRepository.save(mapper.map(genre, Genre.class)));
        return new ResponseEntity<>(mapper.map(saved, GenreDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<GenreDTO>> getByArtistId(Long artistId) {
        List<Genre> genres = (List<Genre>) genreRepository.findByArtistId(artistId);
        List<GenreDTO> genreDTOS = genres.stream().map(genre -> mapper.map(genre, GenreDTO.class)).toList();
        return new ResponseEntity<>(genreDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<GenreDTO>> getAll() {
        List<Genre> genres = (List<Genre>) genreRepository.findAll();
        List<GenreDTO> genreDTOS = genres.stream().map(genre -> mapper.map(genre, GenreDTO.class)).toList();
        return new ResponseEntity<>(genreDTOS, HttpStatus.OK);
    }
}
