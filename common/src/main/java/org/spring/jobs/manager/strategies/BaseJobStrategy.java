package org.spring.jobs.manager.strategies;

import lombok.NonNull;
import org.spring.jobs.manager.Job;
import org.spring.jobs.manager.JobStatus;
import org.spring.jobs.manager.details.JobDetail;
import reactor.core.publisher.Mono;

public abstract class BaseJobStrategy<T extends JobDetail> implements JobStrategy<T> {

    @SuppressWarnings("unchecked")
    @Override
    public final Mono<Job<T>> run(@NonNull Job<T> job) {
        return Mono.fromCallable(() -> {
            execute(job.getJobDetail());
            return job.toStatus(JobStatus.SUCCESS);
        });
    }

    protected abstract void execute(T jobDetail);
}
