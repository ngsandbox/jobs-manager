package org.spring.jobs.manager.strategies;

import lombok.NonNull;
import org.spring.jobs.manager.Job;
import org.spring.jobs.manager.details.JobDetail;
import reactor.core.publisher.Mono;

public interface JobStrategy<T extends JobDetail> {

    /**
     * Unique code of the strategy
     */
    String getCode();

    /**
     * The description of the strategy
     */
    String getDescription();

    /**
     * Execution of the job
     * @param job job's infor with details and schedule info
     */
    Mono<Job<T>> run(@NonNull Job<T> job);
}
