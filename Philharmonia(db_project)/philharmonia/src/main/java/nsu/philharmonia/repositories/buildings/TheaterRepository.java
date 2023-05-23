package nsu.philharmonia.repositories.buildings;

import nsu.philharmonia.model.entities.buildings.Building;
import nsu.philharmonia.model.entities.buildings.Theater;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends CrudRepository<Theater, Building> {}