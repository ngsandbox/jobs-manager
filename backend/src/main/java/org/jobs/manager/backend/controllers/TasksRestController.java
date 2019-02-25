package org.jobs.manager.backend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.backend.models.JobInfo;
import org.jobs.manager.backend.models.TaskInfo;
import org.jobs.manager.backend.models.TaskModel;
import org.jobs.manager.common.services.TaskService;
import org.jobs.manager.common.shared.TaskMetadata;
import org.jobs.manager.common.shared.TaskStrategy;
import org.jobs.manager.common.shared.utils.JsonHelper;
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
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/v1/")
public class TasksRestController {

    private final TaskService taskService;

    private final SubscriptionService subscriptionService;

    private final SimpMessagingTemplate webSocket;

    private final List<TaskStrategy> strategies;

    @Autowired
    public TasksRestController(TaskService taskService,
                               List<TaskStrategy> strategies,
                               SubscriptionService subscriptionService,
                               SimpMessagingTemplate webSocket) {
        this.strategies = strategies;
        this.taskService = taskService;
        this.subscriptionService = subscriptionService;
        this.webSocket = webSocket;
    }

    @GetMapping("/tasks/metadata/{strategyCode}")
    public Flux<TaskMetadata> tasksMetadata(@PathVariable String strategyCode) {
        log.debug("Provide tasks metadata for strategy code {}", strategyCode);
        return Flux.fromStream(strategies.stream()
                .filter(s -> s.getCode().equals(strategyCode))
                .map(TaskStrategy::getTaskMetadata));
    }

    @GetMapping("/tasks")
    public Flux<TaskModel> tasks() {
        log.debug("Get list of all tasks");
        return taskService.getTasks()
                .map(TaskModel::toModel);
    }

    @PostMapping(path = "/tasks")
    public void createTask(@Valid @RequestBody TaskInfo task) {
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
        webSocket.convertAndSend("/topic/jobs", jobInfo);
    }
}
