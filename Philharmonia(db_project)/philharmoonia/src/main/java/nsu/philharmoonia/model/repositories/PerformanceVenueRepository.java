package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.buildings.Building;
import nsu.philharmoonia.model.entities.buildings.PerformanceVenue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PerformanceVenueRepository extends CrudRepository<PerformanceVenue, Building> {}