package org.jobs.manager;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.entities.TaskStatus;
import org.jobs.manager.events.JobTopicEvent;
import org.jobs.manager.events.TopicService;
import org.jobs.manager.strategies.TaskStrategy;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.jobs.manager.utils.CloseUtils.closeQuite;

@Slf4j
public class JobExecutor implements AutoCloseable {

    private final TopicService topicService;
    private final ExecutorService executorService;
    private final Map<String, TaskStrategy<? extends Task>> strategies;
    private final Scheduler scheduler;
    private final AtomicInteger slotsCount;

    JobExecutor(int threadCount,
                TopicService topicService,
                List<TaskStrategy<? extends Task>> strategies) {
        this.topicService = topicService;
        slotsCount = new AtomicInteger(threadCount);
        executorService = Executors.newFixedThreadPool(threadCount);
        Map<String, ? extends TaskStrategy<? extends Task>> jobStrategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        TaskStrategy::getCode,
                        o -> o,
                        (o, o2) -> {
                            throw new IllegalStateException("Collision detected for strategy code " + o.getCode());
                        }));
        this.strategies = Collections.unmodifiableMap(jobStrategyMap);
        this.scheduler = Schedulers.fromExecutorService(executorService);
    }

    /**
     * Get available slots count to take new jobs
     */
    int getSlotsCount() {
        return slotsCount.get();
    }

    <T extends Task> Publisher<Job<T>> run(Job<T> job) {
        log.debug("Start job {}", job);
        if (executorService.isShutdown()) {
            log.error("The job service is already closed for running job {}", job);
            throw new IllegalStateException("The job service is already closed for running jobs");
        }

        final TaskStrategy taskStrategy = strategies.get(job.getTask().getStrategyCode());
        if (taskStrategy == null) {
            return justError(job, String.format("Strategy with code %s was not found for job id %s", job.getTask().getStrategyCode(), job.getId()));
        }
        if (!job.getTaskSchedule().getSchedule().isReady()) {
            log.trace("Task is not ready to run {}", job.getId());
            return Mono.empty();
        }

        return executeTask(job, taskStrategy);
    }

    @SuppressWarnings("unchecked")
    private <T extends Task> Publisher<Job<T>> executeTask(Job<T> job, TaskStrategy<T> taskStrategy) {
        try {
            slotsCount.decrementAndGet(); //decrement available slot

            Flux<Job<T>> flux = Flux.concat(
                    Mono.just(job.toStatus(TaskStatus.RUNNING)),
                    Mono.fromRunnable(() -> taskStrategy.execute(job.getTask())),
                    Mono.just(job.toStatus(TaskStatus.SUCCESS))

            );
            Flux<Job<T>> onErrorFlux = flux
                    .doOnNext(tJob -> topicService.publish(new JobTopicEvent(tJob)))
                    .doOnComplete(slotsCount::incrementAndGet) // increment back available slots
                    .onErrorResume((ex) -> onFluxError(job, ex));
            return onErrorFlux.subscribeOn(scheduler);
        } catch (Exception ex) {
            slotsCount.incrementAndGet(); // increment back available slots
            log.error("Fatal error during the execution for strategy {} and job {}", taskStrategy, job, ex);
            return justError(job,
                    String.format("Fatal error for job id %s. Error: %s", job.getId(), ex.getMessage()));
        }
    }

    private <T extends Task> Mono<Job<T>> justError(Job<T> job, String error) {
        Mono<Job<T>> just = Mono.just(job.toStatus(TaskStatus.FAILED, error));
        return just.doOnNext(j -> topicService.publish(new JobTopicEvent(j)));
    }

    private <T extends Task> Mono<Job<T>> onFluxError(Job<T> job, Throwable ex) {
        log.error("Error to execute job {}", job, ex);
        return Mono.just(job.toStatus(TaskStatus.FAILED, String.format("Error to execute job id %s. Error %s", job.getId(), ex.getMessage())));
    }


    @Override
    public void close() {
        log.info("Close the job service");
        if (!executorService.isShutdown()) {
            closeQuite(executorService::shutdown);
        }
    }
}