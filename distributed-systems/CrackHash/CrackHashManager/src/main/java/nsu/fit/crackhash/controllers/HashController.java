package nsu.fit.crackhash.controllers;

import nsu.fit.crackhash.config.Constants;
import nsu.fit.crackhash.model.dto.CrackResponseDto;
import nsu.fit.crackhash.model.dto.HashDto;
import nsu.fit.crackhash.model.dto.StatusResponseDto;
import nsu.fit.crackhash.services.HashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 1440)
@Validated
@RestController
public class HashController {
    private final HashService hashService;
    private final Logger logger = LoggerFactory.getLogger(HashController.class);

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping(Constants.MANAGER_BASE_API_PATH + "/crack")
    public CrackResponseDto crackHash(@RequestBody HashDto dto) {
        logger.info("Got a request to crack hash {}", dto.getHash());
        return hashService.crackHash(dto);
    }

    @GetMapping(Constants.MANAGER_BASE_API_PATH + "/status")
    public StatusResponseDto getStatus(@RequestParam String requestId) {
        logger.info("Got a request to get status of {} request", requestId);
        return hashService.getStatus(requestId);
    }
}
