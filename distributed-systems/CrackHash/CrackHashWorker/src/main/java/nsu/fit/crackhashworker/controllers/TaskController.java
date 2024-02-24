package nsu.fit.crackhashworker.controllers;

import jakarta.servlet.http.HttpServletResponse;
import nsu.fit.crackhashworker.config.Constants;
import nsu.fit.crackhashworker.model.dto.CrackHashManagerRequest;
import nsu.fit.crackhashworker.services.TaskService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 1440)
@Validated
@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/internal/api/worker/hash/crack/task")
    public void createTask(@RequestBody CrackHashManagerRequest request) {
        taskService.crackHash(request);
    }
}
