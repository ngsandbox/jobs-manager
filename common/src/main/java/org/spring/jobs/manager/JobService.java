package org.spring.jobs.manager;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.spring.jobs.manager.details.JobDetail;
import org.spring.jobs.manager.strategies.JobStrategy;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class JobService implements AutoCloseable {

    private final ExecutorService executorService;
    private final Map<String, JobStrategy<? extends JobDetail>> strategies;
    private final Scheduler scheduler;

    public JobService(int threadCount, @NonNull List<JobStrategy<? extends JobDetail>> strategies) {
        executorService = Executors.newFixedThreadPool(threadCount);
        Map<String, ? extends JobStrategy<? extends JobDetail>> jobStrategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        JobStrategy::getCode,
                        o -> o,
                        (o, o2) -> {
                            throw new JobException("Collision detected for strategy code " + o.getCode());
                        }));
        this.strategies = Collections.unmodifiableMap(jobStrategyMap);
        this.scheduler = Schedulers.fromExecutorService(executorService);
    }


    public <T extends JobDetail> Mono<Job<T>> run(Job<T> job) {
        log.debug("Start job {}", job);
        if (executorService.isShutdown()) {
            log.error("The job service is already closed for running job {}", job);
            throw new IllegalStateException("The job service is already closed for running jobs");
        }
        final JobStrategy jobStrategy = strategies.get(job.getJobDetail().getStrategyCode());
        if (jobStrategy == null) {
            return Mono.just(job.toStatus(JobStatus.FAILED,
                    String.format("Strategy with code %s was not found for job id %s", job.getJobDetail().getStrategyCode(), job.getId())));
        }
        if (!job.getJobDetail().getSchedule().isReady()) {
            return Mono.just(job.toStatus(JobStatus.QUEUED,
                    String.format("Job %s is not ready to run", job.getId())));
        }

        return runJob(job, jobStrategy);
    }

    @SuppressWarnings("unchecked")
    private <T extends JobDetail> Mono<Job<T>> runJob(Job<T> job, JobStrategy<T> jobStrategy) {
        try {

            Mono<Job<T>> runMono = jobStrategy.run(job);
            Mono<Job<T>> successMono = runMono
                    .map(o -> o.toStatus(JobStatus.SUCCESS));
            Mono<Job<T>> onErrorMono = successMono
                    .onErrorResume((ex) -> onJobError(job, ex));
            return onErrorMono.subscribeOn(scheduler);
        } catch (Exception ex) {
            log.error("Fatal error during the execution for strategy {} and job {}", jobStrategy, job, ex);
            return Mono.just(job.toStatus(JobStatus.FAILED,
                    String.format("Fatal error for job id %s. Error: %s", job.getId(), ex.getMessage())));
        }
    }

    private <T extends JobDetail> Mono<Job<T>> onJobError(Job<T> job, Throwable ex) {
        log.error("Error to execute job {}", job, ex);
        return Mono.just(job.toStatus(JobStatus.FAILED,
                String.format("Error to execute job id %s. Error %s", job.getId(), ex.getMessage())));
    }

    @Override
    public void close() {
        log.info("Close the job service");
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
