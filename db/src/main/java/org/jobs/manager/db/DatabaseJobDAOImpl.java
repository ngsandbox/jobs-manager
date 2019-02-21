package org.jobs.manager.db;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.dao.JobDAO;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.schedulers.Scheduler;
import org.jobs.manager.db.model.JobHistoryEntity;
import org.jobs.manager.db.model.ScheduleEntity;
import org.jobs.manager.db.model.TaskEntity;
import org.jobs.manager.db.repositories.JobHistoryRepository;
import org.jobs.manager.db.repositories.ScheduleRepository;
import org.jobs.manager.db.repositories.TaskDetailRepository;
import org.jobs.manager.db.repositories.TaskRepository;
import org.jobs.manager.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@Component
public class DatabaseJobDAOImpl implements JobDAO {

    private final JobHistoryRepository jobHistoryRepository;
    private final TaskRepository taskRepository;
    private final ScheduleRepository scheduleRepository;
    private final TaskDetailRepository taskDetailRepository;

    @Autowired
    public DatabaseJobDAOImpl(JobHistoryRepository jobHistoryRepository,
                              TaskRepository taskRepository,
                              ScheduleRepository scheduleRepository,
                              TaskDetailRepository taskDetailRepository) {
        this.jobHistoryRepository = jobHistoryRepository;
        this.taskRepository = taskRepository;
        this.scheduleRepository = scheduleRepository;
        this.taskDetailRepository = taskDetailRepository;
    }

    @Override
    public Flux<Job<Task>> takeJobs(int limit) {
        if (limit <= 0) {
            return Flux.empty();
        }

        List<TaskEntity> activeTasks = taskRepository.findActiveTasks(LocalDateTime.now(), PageRequest.of(0, limit));
        return Flux.fromStream(activeTasks.stream())
                .log()
                .doOnNext(s -> s.getSchedule().setActive(false))
                .doOnNext(t -> scheduleRepository.save(t.getSchedule()))
                //.map(ScheduleEntity::getTask)
                .map(TaskEntity::toTask)
                .map(tuple -> JobHistoryEntity.queued(tuple.getT1(), tuple.getT2()))
                .doOnNext(j -> jobHistoryRepository.save(JobHistoryEntity.from(j)));
    }

    @Override
    public void updateTaskScheduler(@NonNull String taskId, @NonNull Scheduler scheduler) {
        log.debug("Update for taskId {} scheduler {}", taskId);
        ScheduleEntity scheduleEntity = ScheduleEntity.from(taskId, scheduler);
        scheduleRepository.save(scheduleEntity);
    }

    @Override
    public Flux<Job<Task>> getJobHistory(@NonNull String jobId) {
        log.debug("Find job's history by job identifier {}", jobId);
        return Flux.fromStream(jobHistoryRepository.findByJobId(jobId).stream())
                .log()
                .map(JobHistoryEntity::toJob);
    }

    @Override
    public Flux<Job<Task>> getJobHistoryByTaskId(@NonNull String taskId) {
        log.debug("Find jobs history by task identifier {}", taskId);
        List<JobHistoryEntity> byTaskId = jobHistoryRepository.findByTaskId(taskId);
        return Flux.fromStream(byTaskId.stream())
                .log()
                .map(JobHistoryEntity::toJob);
    }

    @Override
    public void save(@NonNull Task task, @NonNull Scheduler scheduler) {
        log.debug("Save task {} and schedule {}", task.getId(), scheduler.getId());
        TaskEntity taskEntity = TaskEntity.from(task, scheduler);
//        taskDetailRepository.saveAll(taskEntity.getDetails());
//        scheduleRepository.save(taskEntity.getSchedule());
        taskRepository.save(taskEntity);
        log.debug("Task saved {}", task.getId());
    }

    @Override
    public Mono<Task> getTask(@NonNull String taskId) {
        log.debug("Find task by id {}", taskId);
        Optional<TaskEntity> taskEntity = taskRepository.findByTaskId(taskId);
        return Mono.justOrEmpty(taskEntity)
                .map(d -> d.toTask().getT1());
    }

    @Override
    public void save(@NonNull Job<Task> job) {
        log.debug("Save job history {}", job);
        JobHistoryEntity from = JobHistoryEntity.from(job);
        jobHistoryRepository.save(from);
        log.debug("Job saved {}", job.getId());
    }
}
