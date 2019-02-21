package org.jobs.manager.backend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.schedulers.Scheduler;
import org.jobs.manager.common.services.TaskService;
import org.jobs.manager.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/v1/")
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

    @PostMapping("/tasks")
    public void createTask(@RequestParam Task task, @RequestParam Scheduler scheduler) {
        log.debug("Save task info {} and scheduler {}", task, scheduler);
        taskService.saveTask(task, scheduler);
    }

    @DeleteMapping("/tasks/{taskId}")
    public void updateTask(@PathVariable String taskId, @RequestParam Task task, @RequestParam Scheduler scheduler) {
        log.debug("Update task {}", taskId);
        taskService.saveTask(task, scheduler);
    }

    @GetMapping("/tasks/{taskId}")
    public Flux<Job<Task>> getHistory(@PathVariable String taskId) {
        log.debug("Get jobs history for task id {}", taskId);
        return taskService.getJobs(taskId);
    }
}
