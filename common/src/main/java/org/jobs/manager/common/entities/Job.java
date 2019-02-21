package org.jobs.manager.common.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.schedulers.Scheduler;
import org.jobs.manager.entities.Task;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Job<T extends Task> {

    /**
     * Job identifier
     */
    private final String id;

    private final T task;

    private final LocalDateTime started;

    private final TaskStatus status;

    private final String description;

    private Scheduler scheduler;

    private Job(String id,
                Scheduler scheduler,
                LocalDateTime started,
                T task,
                TaskStatus status,
                String description) {
        this.id = id;
        this.scheduler = scheduler;
        this.task = task;
        this.started = started;
        this.status = status;
        this.description = description;
    }

    public static <T extends Task> Job<T> queued(@NonNull T task, @NotNull Scheduler scheduler) {
        return new Job<>(UUID.randomUUID().toString(), scheduler, LocalDateTime.now(), task, TaskStatus.QUEUED, null);
    }

    public static <T extends Task> Job<T> toJob(
            @NonNull String id,
            @NonNull LocalDateTime started,
            @NonNull T task,
            @NonNull Scheduler scheduler,
            @NonNull TaskStatus status,
            String description) {
        return new Job<>(id, scheduler, started, task, status, description);
    }

    public Job<T> toStatus(@NonNull TaskStatus status) {
        return toStatus(status, null);
    }

    public Job<T> toStatus(@NonNull TaskStatus status, String description) {
        log.info("Move to the next status {} with description {} for current job id {}", status, description, id);
        return new Job<>(id, scheduler, started, task, status, description);
    }
}
