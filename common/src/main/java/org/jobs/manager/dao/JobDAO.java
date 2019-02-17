package org.jobs.manager.dao;

import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.schedulers.Scheduler;

import java.util.List;

public interface JobDAO {

    <T extends Task> List<Job<T>> takeJobs(int limit);

    <T extends Task> void updateTaskScheduler(T task, Scheduler startDate);

    List<Job<? extends Task>> getJobHistory(String jobId);

    List<Job<? extends Task>> getTaskHistory(String taskId);

    void save(Job<Task> job);
}
