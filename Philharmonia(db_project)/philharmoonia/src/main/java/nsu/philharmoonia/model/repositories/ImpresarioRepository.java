package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.Impresario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ImpresarioRepository extends CrudRepository<Impresario, Long> {}