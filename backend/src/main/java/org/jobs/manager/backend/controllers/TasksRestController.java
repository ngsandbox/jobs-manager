package org.jobs.manager.backend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.backend.models.JobInfo;
import org.jobs.manager.backend.models.TaskInfo;
import org.jobs.manager.backend.models.TaskModel;
import org.jobs.manager.common.services.TaskService;
import org.jobs.manager.common.subscription.SubscriptionService;
import org.jobs.manager.common.subscription.Topics;
import org.jobs.manager.common.subscription.events.JobSubscriptionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1/")
public class TasksRestController {

    private final TaskService taskService;

    private final SubscriptionService subscriptionService;

    private final SimpMessagingTemplate webSocket;

    @Autowired
    public TasksRestController(TaskService taskService,
                               SubscriptionService subscriptionService,
                               SimpMessagingTemplate webSocket) {
        this.taskService = taskService;
        this.subscriptionService = subscriptionService;
        this.webSocket = webSocket;
    }

    @GetMapping("/tasks")
    public Flux<TaskModel> tasks() {
        log.debug("Get list of all tasks");
        return taskService.getTasks()
                .map(TaskModel::toModel);
    }

    @PostMapping(path = "/tasks")
    public void createTask(@RequestBody(required = false) @Valid TaskInfo task) {
        log.debug("Save info {}", task);
        taskService.saveTask(task.getTask().takeTask(), task.getScheduler().takeScheduler());
    }

    @DeleteMapping("/tasks/{taskId}")
    public void deleteTask(@PathVariable String taskId) {
        log.debug("Update task {}", taskId);
        taskService.deleteTask(taskId);
    }

    @GetMapping("/tasks/{taskId}")
    public Mono<TaskInfo> getTask(@PathVariable String taskId) {
        log.debug("Get task id {}", taskId);
        return taskService.getTaskInfo(taskId)
                .map(t -> TaskInfo.from(t.getT1(), t.getT2()));
    }

    @GetMapping(value = "/jobs/listen", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void listenStrory() {
        log.debug("Listen to jobs changes ");
        subscriptionService.subscribe(Topics.JOB_TOPIC)
                .filter(s -> s instanceof JobSubscriptionEvent)
                .map(s -> (JobSubscriptionEvent) s)
                .doOnNext(this::emitJob)
                .subscribe();
    }

    private void emitJob(JobSubscriptionEvent event) {
        log.debug("Send message to subscriber's websockets {}", event);
        JobInfo jobInfo = JobInfo.from(event.getJob());
        webSocket.convertAndSend("/topic/jobs/" + jobInfo.getStatus(), jobInfo);
    }
}
