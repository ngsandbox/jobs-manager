package org.jobs.manager.common.stubs;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.dao.JobDAO;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.shared.Task;
import org.jobs.manager.common.schedulers.Scheduler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class TestJobsDAOImpl implements JobDAO {

    private final List<Job<Task>> history = new CopyOnWriteArrayList<>();

    private final Job<Task> cronTestJob = Tasks.getCronTestJob("*/2 * * * * *", 0, null, false);

    private final Set<String> busyTasks = ConcurrentHashMap.newKeySet();


    @Override
    public Flux<Job<Task>> takeJobs(int limit) {
        log.debug("Take available jobs count {}", limit);
        return Flux.just(cronTestJob)
                .filter(t -> t.getScheduler().isActive())
                .doOnNext(j -> busyTasks.add(j.getTask().getId()))
                .take(limit)
                .log();
    }

    @Override
    public void updateTaskScheduler(@NonNull String taskId, @NonNull Scheduler scheduler) {
        log.debug("Release task {} and scheduler {}", taskId, scheduler);
        busyTasks.remove(taskId);
        if (cronTestJob.getId().equals(taskId)) {
            cronTestJob.setScheduler(scheduler);
        }
    }

    @Override
    public Flux<Job<Task>> getJobHistory(String jobId) {
        log.debug("Find job history by id {}", jobId);
        return Flux.fromStream(this.history.stream()
                .filter(h -> h.getId().equals(jobId)));
    }

    @Override
    public Flux<Job<Task>> getJobHistoryByTaskId(String taskId) {
        log.debug("Find jobs history for task id {}", taskId);
        return Flux.fromStream(this.history.stream())
                .filter(h -> h.getTask().getId().equals(taskId));
    }

    @Override
    public void save(@NonNull Task task, @NonNull Scheduler schedule) {
        history.add(Job.queued(task, schedule));
    }

    @Override
    public Mono<Task> getTask(String taskId) {
        return Mono.empty();
    }

    @Override
    public Mono<Tuple2<Task, Scheduler>> getTaskInfo(String taskId) {
        if (cronTestJob.getTask().getId().equals(taskId)) {
            return Mono.just(Tuples.of(cronTestJob.getTask(), cronTestJob.getScheduler()));
        }

        return Mono.empty();
    }

    @Override
    public Flux<Task> getTasks() {
        return Flux.just(cronTestJob.getTask());
    }

    @Override
    public void save(Job<Task> job) {
        history.add(job);
    }

    @Override
    public void deleteTask(String taskId) {
        log.warn("Not implemented");
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Flux<Tuple2<Task, Scheduler>> getTaskInfos() {
        return Flux.just(Tuples.of(cronTestJob.getTask(), cronTestJob.getScheduler()));
    }
}
