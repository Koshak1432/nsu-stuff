package nsu.fit.crackhash.controllers;

import nsu.fit.crackhash.config.Constants;
import nsu.fit.crackhash.model.dto.CrackHashWorkerResponse;
import nsu.fit.crackhash.model.dto.CrackResponseDto;
import nsu.fit.crackhash.model.dto.HashDto;
import nsu.fit.crackhash.model.dto.StatusResponseDto;
import nsu.fit.crackhash.services.HashService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 1440)
@Validated
@RestController
public class HashController {
    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping( Constants.MANAGER_BASE_API_PATH + "/crack")
    public CrackResponseDto crackHash(@RequestBody HashDto dto) {
        return hashService.crackHash(dto);
    }

    @GetMapping(Constants.MANAGER_BASE_API_PATH + "/status")
    public StatusResponseDto getStatus(@RequestParam String requestId) {
        return hashService.getStatus(requestId);
    }

    @PatchMapping(Constants.WORKER_TO_MANAGER_URL)
    public void updateCrackHashResult(@RequestBody CrackHashWorkerResponse response) {
        hashService.updateAnswers(response);
    }
}
