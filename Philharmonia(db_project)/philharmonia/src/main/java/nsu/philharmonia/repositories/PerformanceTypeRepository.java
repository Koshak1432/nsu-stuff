package nsu.philharmonia.repositories;

import nsu.philharmonia.model.entities.PerformanceType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PerformanceTypeRepository extends CrudRepository<PerformanceType, Long> {
    Optional<PerformanceType> findByName(String name);

}
