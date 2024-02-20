package nsu.fit.crackhashworker.services;

import nsu.fit.crackhashworker.model.dto.CrackHashManagerRequest;

public interface TaskService {
    Void crackHash(CrackHashManagerRequest request);
}
