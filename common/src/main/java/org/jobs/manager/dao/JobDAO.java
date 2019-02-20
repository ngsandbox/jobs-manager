package org.jobs.manager.dao;

import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.schedulers.Scheduler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface JobDAO {

    Flux<Job<Task>> takeJobs(int limit);

    void updateTaskScheduler(Task task, Scheduler startDate);

    Flux<Job<Task>> getJobHistory(String jobId);

    Flux<Job<Task>> getTaskHistory(String taskId);

    void save(Task task, Scheduler schedule);

    Mono<Task> getTask(String taskId);

    void save(Job<Task> job);
}
