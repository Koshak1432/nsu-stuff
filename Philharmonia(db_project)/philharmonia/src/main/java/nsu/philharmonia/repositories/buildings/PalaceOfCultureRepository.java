package nsu.philharmonia.repositories.buildings;

import nsu.philharmonia.model.entities.buildings.Building;
import nsu.philharmonia.model.entities.buildings.PalaceOfCulture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PalaceOfCultureRepository extends CrudRepository<PalaceOfCulture, Building> {
    Optional<PalaceOfCulture> findByBuildingName(String name);
}