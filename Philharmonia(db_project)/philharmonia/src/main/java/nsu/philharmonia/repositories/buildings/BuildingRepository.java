package nsu.philharmonia.repositories.buildings;

import nsu.philharmonia.model.entities.buildings.Building;
import nsu.philharmonia.model.entities.buildings.Estrade;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends CrudRepository<Building, Long> {

}