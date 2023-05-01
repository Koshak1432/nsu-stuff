package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.Artist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends CrudRepository<Artist, Long> {}