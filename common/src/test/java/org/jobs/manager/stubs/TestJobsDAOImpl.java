package org.jobs.manager.stubs;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.TestTask;
import org.jobs.manager.dao.JobDAO;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.entities.TaskSchedule;
import org.jobs.manager.schedulers.CronScheduler;
import org.jobs.manager.schedulers.Scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TestJobsDAOImpl implements JobDAO {

    private final List<Job<? extends Task>> history = new CopyOnWriteArrayList<>();

    private <T extends Task> Job<T> getCronTestJob(String pattern, int priority, Integer timeout, boolean throwError) {
        TestTask task = TestTask.builder()
                .id(UUID.randomUUID().toString())
                .strategyCode(TestTaskStrategyImpl.TEST_STRATEGY_CODE)
                .timeoutSecs(timeout)
                .throwError(throwError)
                .build();
        TaskSchedule taskSchedule = TaskSchedule.builder()
                .id(UUID.randomUUID().toString())
                .taskId(task.getId())
                .priority(priority)
                .schedule(new CronScheduler(pattern))
                .build();
        return Job.queued((T) task, taskSchedule);
    }

    private final Set<String> busyTasks = ConcurrentHashMap.newKeySet();

    private final Job<TestTask> cronTestTask = getCronTestJob("*/2 * * * * *", 0, null, false);

    @Override
    public <T extends Task> List<Job<T>> takeJobs(int limit) {
        List<Job<T>> testJobs = Stream.of(cronTestTask)
                .map(j -> (Job<T>) j)
                .filter(j -> !busyTasks.contains(j.getTask().getId()))
                .collect(Collectors.toList());
        busyTasks.addAll(testJobs.stream().map(j -> j.getTask().getId()).collect(Collectors.toList()));
        return testJobs;
    }

    @Override
    public <T extends Task> void updateTaskScheduler(T task, @NonNull Scheduler scheduler) {
        busyTasks.remove(task.getId());
        cronTestTask.getTaskSchedule().setSchedule(scheduler);
    }

    @Override
    public List<Job<? extends Task>> getJobHistory(String jobId) {
        log.debug("Find job history by id {}", jobId);
        return this.history.stream()
                .filter(h -> h.getId().equals(jobId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Job<? extends Task>> getTaskHistory(String taskId) {
        log.debug("Find jobs history for task id {}", taskId);
        return this.history.stream()
                .filter(h -> h.getTask().getId().equals(taskId))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Job<Task> job) {
        history.add(job);
    }
}
