package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.buildings.BuildingType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface BuildingTypeRepository extends CrudRepository<BuildingType, Long> {}