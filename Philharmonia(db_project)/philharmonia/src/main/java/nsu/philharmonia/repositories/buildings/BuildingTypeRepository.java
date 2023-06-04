package nsu.philharmonia.repositories.buildings;

import nsu.philharmonia.model.entities.buildings.BuildingType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BuildingTypeRepository extends CrudRepository<BuildingType, Long> {
    Optional<BuildingType> findByName(String name);
}
