package org.spring.jobs.manager;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.spring.jobs.manager.details.JobDetail;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Job<T extends JobDetail> {

    private final String id;

    private final T jobDetail;

    private final LocalDateTime started;

    private final JobStatus status;

    private final String error;

    private Job(String id, LocalDateTime started, T jobDetail, JobStatus status, String error) {
        this.id = id;
        this.jobDetail = jobDetail;
        this.started = started;
        this.status = status;
        this.error = error;
    }

    public static <T extends JobDetail> Job<T> queued(@NonNull T jobDetail) {
        return new Job<>(UUID.randomUUID().toString(), LocalDateTime.now(), jobDetail, JobStatus.QUEUED, null);
    }

    public Job toStatus(@NonNull JobStatus status) {
        return toStatus(status, null);
    }

    Job<T> toStatus(@NonNull JobStatus status, String description) {
        log.info("Move to the next status {} with description {} for current job id {}", status, description, id);
        return new Job<>(id, started, jobDetail, status, description);
    }
}
