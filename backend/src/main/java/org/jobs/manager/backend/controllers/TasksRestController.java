package org.jobs.manager.backend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.backend.entities.TaskInfo;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.services.TaskService;
import org.jobs.manager.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping(value = "/v1/")
public class TasksRestController {

    private final TaskService taskService;

    @Autowired
    public TasksRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public Flux<Task> tasks() {
        log.debug("Get list of all tasks");
        return taskService.getTasks();
    }

    @PostMapping(path = "/tasks", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void createTask(@RequestBody TaskInfo taskInfo) {
        log.trace("Save info {}", taskInfo);
        taskService.saveTask(taskInfo.getTask(), taskInfo.getScheduler());
    }

    @DeleteMapping("/tasks/{taskId}")
    public void deleteTask(@PathVariable String taskId) {
        log.debug("Update task {}", taskId);
        taskService.deleteTask(taskId);
    }

    @GetMapping("/tasks/{taskId}")
    public Flux<Job<Task>> getHistory(@PathVariable String taskId) {
        log.debug("Get jobs history for task id {}", taskId);
        return taskService.getJobs(taskId);
    }
}
