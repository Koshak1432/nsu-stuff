package nsu.philharmonia.repositories;

import nsu.philharmonia.model.entities.Sponsor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SponsorRepository extends CrudRepository<Sponsor, Long> {
    Optional<Sponsor> findSponsorByNameAndSurname(String name, String surname);
}