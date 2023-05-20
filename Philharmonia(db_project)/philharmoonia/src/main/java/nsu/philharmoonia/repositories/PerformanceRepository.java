package nsu.philharmoonia.repositories;

import nsu.philharmoonia.model.entities.Artist;
import nsu.philharmoonia.model.entities.Performance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository

public interface PerformanceRepository extends CrudRepository<Performance, Long> {
    // TODO концертные мероприятия == всё, что не конкурс? Тут вернёт все мероприятия в здании
    //maybe works without query
    @Query(value = """
            SELECT * FROM performance
            WHERE building_id = :buildingId
            """, nativeQuery = true)
    Collection<Performance> findByBuilding(@Param("buildingId") Long buildingId);


    // todo потом надо притянуть места в другом запросе
    @Query(value = """
            SELECT * FROM artist
            WHERE id IN
            (
            	SELECT artist_id FROM contest_place
            	WHERE performance_id = :contestId AND place <= 3
            	ORDER BY place
            )
            """, nativeQuery = true)
    Collection<Artist> findTop3InContest(@Param("contestId") Long contestId);

}