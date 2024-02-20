package nsu.fit.crackhashworker.controllers;

import nsu.fit.crackhashworker.config.Constants;
import nsu.fit.crackhashworker.model.dto.CrackHashManagerRequest;
import nsu.fit.crackhashworker.services.TaskService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 1440)
@Validated
@RequestMapping(Constants.BASE_API_PATH + "/worker/hash/crack/task")
@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Void createTask(@RequestBody CrackHashManagerRequest request) {
        return taskService.crackHash(request);
    }

}
