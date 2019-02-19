package org.jobs.manager;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.dao.JobDAO;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.BaseSubscriber;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.jobs.manager.utils.CloseUtils.closeQuite;

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
            List<Job<Task>> jobs = jobDAO.takeJobs(slotsCount);
            log.debug("Send for execution jobs {}", jobs);
            for (Job<Task> job : jobs) {
                jobExecutor.run(job)
                        .subscribe(new JobSubscriber(job, jobDAO));
            }
        }
    }

    private static class JobSubscriber extends BaseSubscriber<Job<Task>> {
        private final Job<Task> job;
        private final JobDAO jobDAO;

        private JobSubscriber(Job<Task> job, JobDAO jobDAO) {
            this.job = job;
            this.jobDAO = jobDAO;
        }

        @Override
        public void hookOnNext(Job<Task> taskJob) {
            log.debug("Changed status for jobId {}", job.getId());
            jobDAO.save(taskJob);
        }

        @Override
        public void hookOnError(Throwable ex) {
            log.error("Error catched from publisher for jobId {}", job.getId(), ex);
        }

        @Override
        public void hookOnComplete() {
            log.info("Job completed for id {}", job.getId());
            job.getSchedule().next()
                    .ifPresent(scheduler ->
                            jobDAO.updateTaskScheduler(job.getTask(), scheduler));
        }
    }

    @Override
    public void close() {
        log.warn("Close job's  service resources");
        closeQuite(jobExecutor::close);
        closeQuite(scheduler::shutdown);
    }
}
