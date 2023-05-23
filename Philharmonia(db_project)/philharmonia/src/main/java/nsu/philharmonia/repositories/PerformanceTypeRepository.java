package nsu.philharmonia.repositories;

import nsu.philharmonia.model.entities.PerformanceType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceTypeRepository extends CrudRepository<PerformanceType, Long> {}