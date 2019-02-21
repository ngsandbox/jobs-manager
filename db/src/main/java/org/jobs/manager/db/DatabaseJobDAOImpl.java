package org.jobs.manager.db;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.dao.JobDAO;
import org.jobs.manager.db.model.JobHistoryEntity;
import org.jobs.manager.db.model.ScheduleEntity;
import org.jobs.manager.db.model.TaskEntity;
import org.jobs.manager.db.repositories.JobHistoryRepository;
import org.jobs.manager.db.repositories.ScheduleRepository;
import org.jobs.manager.db.repositories.TaskDetailRepository;
import org.jobs.manager.db.repositories.TaskRepository;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.schedulers.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@Service
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

        return Flux.fromStream(taskRepository.findActiveTasks(LocalDateTime.now(), PageRequest.of(0, limit)).stream())
                .log()
                .doOnNext(s -> s.getSchedule().setActive(false))
                .doOnNext(t -> scheduleRepository.save(t.getSchedule()))
                //.map(ScheduleEntity::getTask)
                .map(TaskEntity::toTask)
                .map(tuple -> JobHistoryEntity.queued(tuple.getT1(), tuple.getT2()))
                .doOnNext(j -> jobHistoryRepository.save(JobHistoryEntity.from(j)));
    }

    @Override
    public void updateTaskScheduler(Task task, Scheduler startDate) {

    }

    @Override
    public Flux<Job<Task>> getJobHistory(String jobId) {
        log.debug("Find job's history by job identifier {}", jobId);
        return Flux.fromStream(jobHistoryRepository.findByJobId(jobId).stream())
                .log()
                .map(JobHistoryEntity::toJob);
    }

    @Override
    public Flux<Job<Task>> getTaskHistory(String taskId) {
        log.debug("Find jobs history by task identifier {}", taskId);
        return Flux.fromStream(jobHistoryRepository.findByTaskId(taskId).stream())
                .log()
                .map(JobHistoryEntity::toJob);
    }

    @Override
    public void save(@NonNull Task task, @NonNull Scheduler scheduler) {
        log.debug("Save task {} and schedule {}", task.getId(), scheduler.getId());
        TaskEntity taskEntity = TaskEntity.from(task, scheduler);
        taskDetailRepository.saveAll(taskEntity.getDetails());
        scheduleRepository.save(taskEntity.getSchedule());
        taskRepository.save(taskEntity);
        log.debug("Task saved {}", task.getId());
    }

    @Override
    public Mono<Task> getTask(String taskId) {
        log.debug("Find task by id {}", taskId);
        log.debug("All tasks {}", taskRepository.findAll());
        log.debug("All task details {}", taskDetailRepository.findAll());
        log.debug("All task schedule {}", scheduleRepository.findAll());
        log.debug("All jobs {}", jobHistoryRepository.findAll());
        Optional<TaskEntity> taskEntity = taskRepository.findByTaskId(taskId);
        return Mono.justOrEmpty(taskEntity)
                .map(d -> d.toTask().getT1());
    }

    @Override
    public void save(Job<Task> job) {
        log.debug("Save job history {}", job);
        jobHistoryRepository.save(JobHistoryEntity.from(job));
    }
}
