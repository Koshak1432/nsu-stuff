package nsu.philharmonia.repositories;

import nsu.philharmonia.model.entities.Artist;
import nsu.philharmonia.model.entities.ContestPlace;
import nsu.philharmonia.model.entities.ContestPlaceKey;
import nsu.philharmonia.model.entities.Performance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContestPlaceRepository extends CrudRepository<ContestPlace, ContestPlaceKey> {

    List<ContestPlace> findContestPlaceByPerformanceId(Long id);

    Optional<ContestPlace> findContestPlaceByArtistAndPerformance(Artist artist, Performance performance);
}