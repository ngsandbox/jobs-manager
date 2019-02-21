package org.jobs.manager.common.dao;

import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.schedulers.Scheduler;
import org.jobs.manager.entities.Task;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface JobDAO {

    Flux<Job<Task>> takeJobs(int limit);

    void updateTaskScheduler(String taskId, Scheduler startDate);

    Flux<Job<Task>> getJobHistory(String jobId);

    Flux<Job<Task>> getJobHistoryByTaskId(String taskId);

    void save(Task task, Scheduler schedule);

    Mono<Task> getTask(String taskId);

    void save(Job<Task> job);
}
