package nsu.philharmonia.repositories;

import nsu.philharmonia.model.entities.ContestPlace;
import nsu.philharmonia.model.entities.ContestPlaceKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestPlaceRepository extends CrudRepository<ContestPlace, ContestPlaceKey> {

    List<ContestPlace> findContestPlaceByPerformanceId(Long id);

}