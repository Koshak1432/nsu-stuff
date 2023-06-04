package nsu.philharmonia.repositories.buildings;

import nsu.philharmonia.model.entities.buildings.Building;
import nsu.philharmonia.model.entities.buildings.BuildingType;
import nsu.philharmonia.model.entities.buildings.Estrade;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends CrudRepository<Building, Long> {

//    @Query(value = """
//            SELECT * FROM building_type
//            WHERE building_type.name = :name
//            """, nativeQuery = true)
//    Optional<BuildingType> findBuildingTypeByName(@Param("name") String name);

    @Query("SELECT new BuildingType(type.id, type.name) FROM BuildingType type where type.name = :name")
    Optional<BuildingType> findBuildingTypeByName(@Param("name") String name);

}