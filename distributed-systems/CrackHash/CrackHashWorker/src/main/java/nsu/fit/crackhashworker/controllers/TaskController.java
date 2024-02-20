package nsu.fit.crackhashworker.controllers;

import nsu.fit.crackhashworker.config.Constants;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(maxAge = 1440)
@Validated
@RequestMapping(Constants.BASE_API_PATH + "/worker/hash/crack/task")
@RestController
public class TaskController {
//    @PostMapping
//    public createTask()

}
