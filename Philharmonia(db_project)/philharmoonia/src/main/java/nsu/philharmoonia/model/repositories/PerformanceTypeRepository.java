package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.PerformanceType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PerformanceTypeRepository extends CrudRepository<PerformanceType, Long> {}