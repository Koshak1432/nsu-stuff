package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.Performance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository

public interface PerformanceRepository extends CrudRepository<Performance, Long> {
   // TODO концертные мероприятия == всё, что не конкурс? Тут вернёт все мероприятия в здании
    @Query(value = """
            SELECT * FROM performance
            WHERE building_id = :buildingId
            """, nativeQuery = true)
    Collection<Performance> findByBuilding(@Param("buildingId") Long buildingId);

}