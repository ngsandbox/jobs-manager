package org.jobs.manager.db;

import lombok.NonNull;
import org.jobs.manager.dao.JobDAO;
import org.jobs.manager.db.repositories.JobRepository;
import org.jobs.manager.db.repositories.TaskRepository;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.schedulers.Scheduler;
import reactor.core.publisher.Mono;

import java.util.List;

public class DatabaseJobDAOImpl implements JobDAO {

    private final JobRepository jobRepository;
    private final TaskRepository taskRepository;

    public DatabaseJobDAOImpl(JobRepository jobRepository,
                              TaskRepository taskRepository) {
        this.jobRepository = jobRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public <T extends Task> List<Job<T>> takeJobs(int limit) {
        return null;
    }

    @Override
    public <T extends Task> void updateTaskScheduler(T task, Scheduler startDate) {

    }

    @Override
    public List<Job<? extends Task>> getJobHistory(String jobId) {
        return null;
    }

    @Override
    public List<Job<? extends Task>> getTaskHistory(String taskId) {
        return null;
    }

    @Override
    public Mono<Task> save(@NonNull Task task, @NonNull Scheduler scheduler) {
        return null;
//        return template.inTransaction()
//                .execute(action ->
//                        action.save(TaskEntity.from(task, scheduler)))
//                .next()
//                .map(d -> d.toTask().getT1());
    }

    @Override
    public Mono<Task> getTask(String taskId) {
        return taskRepository.findById(taskId)
                .map(d -> d.toTask().getT1());
    }

    @Override
    public void save(Job<Task> job) {

    }
}
