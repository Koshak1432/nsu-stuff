package nsu.fit.crackhashworker.services;

import nsu.fit.crackhashworker.model.dto.CrackHashManagerRequest;

public interface TaskService {
    void crackHash(CrackHashManagerRequest request);
}
