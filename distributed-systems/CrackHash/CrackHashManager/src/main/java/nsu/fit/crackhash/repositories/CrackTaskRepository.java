package nsu.fit.crackhash.repositories;

import nsu.fit.crackhash.model.entities.CrackTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CrackTaskRepository extends MongoRepository<CrackTask, String> {

}
