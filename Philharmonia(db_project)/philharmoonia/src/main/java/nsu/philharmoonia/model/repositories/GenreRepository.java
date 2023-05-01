package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.Genre;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface GenreRepository extends CrudRepository<Genre, Long> {}