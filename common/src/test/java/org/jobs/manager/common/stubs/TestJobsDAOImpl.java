package org.jobs.manager.common.stubs;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.dao.JobDAO;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.common.schedulers.Scheduler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class TestJobsDAOImpl implements JobDAO {

    private final List<Job<Task>> history = new CopyOnWriteArrayList<>();

    private final Job<Task> cronTestTask = Tasks.getCronTestJob("*/2 * * * * *", 0, null, false);

    private final Set<String> busyTasks = ConcurrentHashMap.newKeySet();


    @Override
    public Flux<Job<Task>> takeJobs(int limit) {
        log.debug("Take available jobs count {}", limit);
        return Flux.just(cronTestTask)
                .filter(t -> t.getScheduler().isActive())
                .doOnNext(j -> busyTasks.add(j.getTask().getId()))
                .take(limit)
                .log();
    }

    @Override
    public void updateTaskScheduler(@NonNull String taskId, @NonNull Scheduler scheduler) {
        log.debug("Release task {} and scheduler {}", taskId, scheduler);
        busyTasks.remove(taskId);
        if (cronTestTask.getId().equals(taskId)) {
            cronTestTask.setScheduler(scheduler);
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
    public Flux<Task> getTasks() {
        return Flux.just(cronTestTask.getTask());
    }

    @Override
    public void save(Job<Task> job) {
        history.add(job);
    }
}
