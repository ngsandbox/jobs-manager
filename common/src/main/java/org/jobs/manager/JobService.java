package org.jobs.manager;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.dao.JobDAO;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.entities.TaskStatus;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.SignalType;

import javax.validation.constraints.NotNull;
import java.util.Optional;
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
            log.debug("Take available tasks {} and subscribe for updates", slotsCount);
            jobDAO.takeJobs(slotsCount)
                    .log()
                    .flatMap(jobExecutor::run)
                    .subscribe(j -> new JobSubscriber(j, jobDAO));
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
        protected void hookFinally(SignalType type) {
            super.hookFinally(type);
            log.debug("Received signal {} for the task stratedy {} of jobId {}", type, job.getTask().getStrategyCode(), job.getId());
        }

        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            super.hookOnSubscribe(subscription);
            log.debug("Subscribed to the task stratedy {} of jobId {}", job.getTask().getStrategyCode(), job.getId());
        }

        @Override
        protected void hookOnCancel() {
            log.debug("Canceled task stratedy {} of jobId {}", job.getTask().getStrategyCode(), job.getId());
        }

        @Override
        public void hookOnNext(Job<Task> taskJob) {
            Optional<TaskStatus> taskStatus = Optional.ofNullable(taskJob)
                    .map(Job::getStatus);
            log.debug("Changed status {} for task stratedy {} of jobId {}", taskStatus, job.getTask().getStrategyCode(), job.getId());
            jobDAO.save(taskJob);
        }

        @Override
        public void hookOnError(Throwable ex) {
            log.error("Error catched from task stratedy {} for jobId {}", job.getTask().getStrategyCode(), job.getId(), ex);
        }

        @Override
        public void hookOnComplete() {
            log.info("Job completed for id {}", job.getId());
            job.getScheduler().next()
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
