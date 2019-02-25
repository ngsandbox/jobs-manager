package org.jobs.manager.common.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.dao.JobDAO;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.schedulers.Scheduler;
import org.jobs.manager.common.shared.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class TaskService {
    private final JobDAO jobDAO;

    @Autowired
    public TaskService(@NotNull JobDAO jobDAO) {
        this.jobDAO = jobDAO;
    }

    public Flux<Task> getTasks() {
        log.debug("Get list of tasks");
        return jobDAO.getTasks();
    }

    public Flux<Tuple2<Task, Scheduler>> getTaskInfos() {
        log.debug("Get list of tasks with schedulers");
        return jobDAO.getTaskInfos();
    }

    public Flux<Job<Task>> getJobs(String taskId) {
        return jobDAO.getJobHistory(taskId);
    }

    public void saveTask(@NonNull Task task, Scheduler scheduler) {
        jobDAO.save(task, scheduler);
    }

    public void deleteTask(@NonNull String taskId) {
        jobDAO.deleteTask(taskId);
    }

    public Mono<Tuple2<Task, Scheduler>> getTaskInfo(String taskId) {
        return jobDAO.getTaskInfo(taskId);
    }
}
