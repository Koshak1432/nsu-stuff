package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.Sponsor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface SponsorRepository extends CrudRepository<Sponsor, Long> {}