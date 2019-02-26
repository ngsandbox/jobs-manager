package org.jobs.manager.common.services;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.dao.JobDAO;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.entities.TaskStatus;
import org.jobs.manager.common.shared.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.jobs.manager.common.utils.CloseUtils.closeQuite;

/**
 * Jobs service is responsible for scheduling and monitoring tasks activity
 */
@Slf4j
@Service
public class JobService implements AutoCloseable {
    private final JobDAO jobDAO;
    private final JobExecutor jobExecutor;
    private final ScheduledExecutorService scheduler;

    @Autowired
    public JobService(@NotNull JobExecutor jobExecutor,
                      @NotNull JobDAO jobDAO) {
        this.jobDAO = jobDAO;
        this.jobExecutor = jobExecutor;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::setupNewJobs, 1, 1, TimeUnit.SECONDS);
    }

    private void setupNewJobs() {
        int slotsCount = jobExecutor.getSlotsCount();
        if (slotsCount > 0) {
            log.trace("Take available tasks {} and subscribe for updates", slotsCount);
            jobDAO.takeJobs(slotsCount)
                    .flatMap(jobExecutor::run)
                    .doOnNext(this::doOnNext)
                    .subscribe();
        }
    }

    private void doOnNext(Job<Task> job) {
        jobDAO.save(job);
        if (job.getStatus() == TaskStatus.SUCCESS ||
                job.getStatus() == TaskStatus.QUEUED) {
            log.info("Job {} finished or sent to queue again. Try rechedule...", job.getId());
            job.getScheduler().next()
                    .ifPresent(scheduler ->
                            jobDAO.updateTaskScheduler(job.getTask().getId(), scheduler));
        }
    }

    @Override
    public void close() {
        log.warn("Close job's  service resources");
        closeQuite(jobExecutor::close);
        closeQuite(scheduler::shutdown);
    }
}
