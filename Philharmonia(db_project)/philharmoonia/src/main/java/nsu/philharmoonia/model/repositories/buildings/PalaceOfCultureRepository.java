package nsu.philharmoonia.model.repositories.buildings;

import nsu.philharmoonia.model.entities.buildings.Building;
import nsu.philharmoonia.model.entities.buildings.PalaceOfCulture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PalaceOfCultureRepository extends CrudRepository<PalaceOfCulture, Building> {}