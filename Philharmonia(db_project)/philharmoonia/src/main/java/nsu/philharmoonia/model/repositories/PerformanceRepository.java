package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.Performance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PerformanceRepository extends CrudRepository<Performance, Long> {}