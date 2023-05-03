package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.Artist;
import nsu.philharmoonia.model.entities.ContestPlace;
import nsu.philharmoonia.model.entities.ContestPlaceKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ContestPlaceRepository extends CrudRepository<ContestPlace, ContestPlaceKey> {

}