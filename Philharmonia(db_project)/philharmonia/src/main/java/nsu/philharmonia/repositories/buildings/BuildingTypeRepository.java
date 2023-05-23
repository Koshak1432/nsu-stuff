package nsu.philharmonia.repositories.buildings;

import nsu.philharmonia.model.entities.buildings.BuildingType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingTypeRepository extends CrudRepository<BuildingType, Long> {}