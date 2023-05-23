package nsu.philharmonia.repositories.buildings;

import nsu.philharmonia.model.entities.buildings.Building;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends CrudRepository<Building, Long> {}