package nsu.fit.crackhash.repositories;

import nsu.fit.crackhash.model.entities.CrackTask;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CrackTaskRepository extends MongoRepository<CrackTask, String> {
    List<CrackTask> findCrackTasksByIsSentToQueue(boolean isSent);
}
