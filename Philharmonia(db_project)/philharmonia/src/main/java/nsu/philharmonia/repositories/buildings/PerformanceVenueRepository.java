package nsu.philharmonia.repositories.buildings;

import nsu.philharmonia.model.entities.buildings.Building;
import nsu.philharmonia.model.entities.buildings.PerformanceVenue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceVenueRepository extends CrudRepository<PerformanceVenue, Building> {}