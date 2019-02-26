package org.jobs.manager.common.services;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.configs.JobManagerProperties;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.entities.TaskStatus;
import org.jobs.manager.common.shared.Task;
import org.jobs.manager.common.shared.TaskStrategy;
import org.jobs.manager.common.subscription.SubscriptionService;
import org.jobs.manager.common.subscription.events.JobSubscriptionEvent;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.jobs.manager.common.utils.CloseUtils.closeQuite;

/**
 * Job executor responsible for sending for execution job to the strategy
 */
@Slf4j
@Component
public class JobExecutor implements AutoCloseable {

    private final SubscriptionService subscriptionService;
    private final ExecutorService executorService;
    private final Map<String, TaskStrategy<? extends Task>> strategies;
    private final AtomicInteger slotsCount;

    @Autowired
    JobExecutor(JobManagerProperties jobManagerProperties,
                SubscriptionService subscriptionService,
                List<TaskStrategy<? extends Task>> strategies) {
        this.subscriptionService = subscriptionService;
        slotsCount = new AtomicInteger(jobManagerProperties.getSlots());
        executorService = Executors.newFixedThreadPool(jobManagerProperties.getParalelizm());
        Map<String, ? extends TaskStrategy<? extends Task>> jobStrategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        TaskStrategy::getCode,
                        o -> o,
                        (o, o2) -> {
                            throw new IllegalStateException("Collision detected for strategy strategyCode " + o.getCode());
                        }));
        this.strategies = Collections.unmodifiableMap(jobStrategyMap);
    }

    /**
     * Get available slots count to take new jobs
     */
    int getSlotsCount() {
        return slotsCount.get();
    }

    @SuppressWarnings("unchecked")
    public <T extends Task> Publisher<Job<T>> run(Job<T> job) {
        log.debug("Start job {}", job);
        if (executorService.isShutdown()) {
            log.error("The job service is already closed for running job {}", job);
            throw new IllegalStateException("The job service is already closed for running jobs");
        }

        final TaskStrategy taskStrategy = strategies.get(job.getTask().getStrategyCode());
        if (taskStrategy == null) {
            return justError(job, String.format("Strategy with strategyCode %s was not found for job id %s", job.getTask().getStrategyCode(), job.getId()));
        }

        if (!job.getScheduler().isReady()) {
            log.warn("Task is not ready to run {}. Now: {}, scheduled: {}", job.getId(), LocalDateTime.now(), job.getScheduler().getStartDate());
            return Flux.just(job);
        }

        return executeTask(job, taskStrategy);
    }

    private <T extends Task> Publisher<Job<T>> executeTask(Job<T> job, TaskStrategy<T> taskStrategy) {
        try {
            slotsCount.decrementAndGet(); //decrement available slot

            Flux<Job<T>> flux = Flux.concat(
                    Mono.just(job.toStatus(TaskStatus.RUNNING)),
                    taskStrategy.execute(job.getTask())
                            .flatMap(aVoid -> Mono.empty()),
                    Mono.just(job.toStatus(TaskStatus.SUCCESS))
            );

            return flux
                    .doOnNext(tJob -> subscriptionService.publish(new JobSubscriptionEvent(tJob, false)))
                    .doOnComplete(slotsCount::incrementAndGet) // increment back available slots
                    .onErrorResume((ex) -> onFluxError(job, ex));
        } catch (Exception ex) {
            slotsCount.incrementAndGet(); // increment back available slots
            log.error("Fatal error during the execution for strategy {} and job {}", taskStrategy, job, ex);
            return justError(job,
                    String.format("Fatal error for job id %s. Error: %s", job.getId(), ex.getMessage()));
        }
    }

    private <T extends Task> Mono<Job<T>> justError(Job<T> job, String error) {
        log.error("Error during job execution {}. {}", error, job);
        Mono<Job<T>> just = Mono.just(job.toStatus(TaskStatus.FAILED, error));
        return just.doOnNext(j -> subscriptionService.publish(new JobSubscriptionEvent(j, true)));
    }

    private <T extends Task> Mono<Job<T>> onFluxError(Job<T> job, Throwable ex) {
        log.error("Error to execute job {}", job, ex);
        return Mono.just(job.toStatus(TaskStatus.FAILED, String.format("Error to execute job id %s. Error %s", job.getId(), ex.getMessage())));
    }


    @Override
    public void close() {
        log.warn("Close the job executor");
        if (!executorService.isShutdown()) {
            closeQuite(executorService::shutdown);
        }
    }
}
