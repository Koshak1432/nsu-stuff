package nsu.philharmoonia.model.repositories.buildings;

import nsu.philharmoonia.model.entities.buildings.Building;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface BuildingRepository extends CrudRepository<Building, Long> {}