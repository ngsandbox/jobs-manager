package org.jobs.manager.common.dao;

import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.schedulers.Scheduler;
import org.jobs.manager.common.shared.Task;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface JobDAO {

    Flux<Job<Task>> takeJobs(int limit);

    void updateTaskScheduler(String taskId, Scheduler startDate);

    Flux<Job<Task>> getJobHistory(String jobId);

    Flux<Job<Task>> getJobHistoryByTaskId(String taskId);

    void save(Task task, Scheduler schedule);

    Mono<Task> getTask(String taskId);

    Mono<Tuple2<Task, Scheduler>> getTaskInfo(String taskId);

    Flux<Task> getTasks();

    void save(Job<Task> job);

    void deleteTask(String taskId);
}
