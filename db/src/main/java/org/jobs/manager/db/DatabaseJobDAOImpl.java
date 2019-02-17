package org.jobs.manager.db;

import org.jobs.manager.dao.JobDAO;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.schedulers.Scheduler;

import java.util.List;

public class DatabaseJobDAOImpl implements JobDAO {
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
    public void save(Job<Task> job) {

    }
}
