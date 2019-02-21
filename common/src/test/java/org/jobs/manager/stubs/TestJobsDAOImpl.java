package org.jobs.manager.stubs;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.TestTask;
import org.jobs.manager.dao.JobDAO;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.schedulers.Scheduler;
import org.jobs.manager.schedulers.Schedulers;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class TestJobsDAOImpl implements JobDAO {

    private final List<Job<Task>> history = new CopyOnWriteArrayList<>();

    private final Job<Task> cronTestTask = getCronTestJob("*/2 * * * * *", 0, null, false);

    private Job<Task> getCronTestJob(String pattern, int priority, Integer timeout, boolean throwError) {
        TestTask task = TestTask.testBuilder()
                .id(UUID.randomUUID().toString())
                .strategyCode(TestTaskStrategyImpl.TEST_STRATEGY_CODE)
                .timeoutSecs(timeout)
                .throwError(throwError)
                .build();

        return Job.queued(task, Schedulers.getCronScheduler(UUID.randomUUID().toString(), pattern, priority));
    }

    @Override
    public Flux<Job<Task>> takeJobs(int limit) {
        log.debug("Take available jobs count {}", limit);
        return Flux.just(cronTestTask)
                .take(limit)
                .log();
    }

    @Override
    public void updateTaskScheduler(@NonNull Task task, @NonNull Scheduler scheduler) {
        log.debug("Release task {} and scheduler {}", task, scheduler);
        if (cronTestTask.getId().equals(task.getId())) {
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
    public Flux<Job<Task>> getTaskHistory(String taskId) {
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
    public void save(Job<Task> job) {
        history.add(job);
    }
}
