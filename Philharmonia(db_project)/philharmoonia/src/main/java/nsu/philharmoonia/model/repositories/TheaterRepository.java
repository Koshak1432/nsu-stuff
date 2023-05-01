package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.buildings.Building;
import nsu.philharmoonia.model.entities.buildings.Theater;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface TheaterRepository extends CrudRepository<Theater, Building> {}