package org.jobs.manager.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

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

    private final TaskSchedule taskSchedule;

    private final T task;

    private final LocalDateTime started;

    private final TaskStatus status;

    private final String error;

    private Job(String id,
                TaskSchedule taskSchedule,
                LocalDateTime started,
                T task,
                TaskStatus status,
                String error) {
        this.id = id;
        this.taskSchedule = taskSchedule;
        this.task = task;
        this.started = started;
        this.status = status;
        this.error = error;
    }

    public static <T extends Task> Job<T> queued(@NonNull T task, @NotNull TaskSchedule schedule) {
        return new Job<>(UUID.randomUUID().toString(), schedule, LocalDateTime.now(), task, TaskStatus.QUEUED, null);
    }

    public Job<T> toStatus(@NonNull TaskStatus status) {
        return toStatus(status, null);
    }

    public Job<T> toStatus(@NonNull TaskStatus status, String description) {
        log.info("Move to the next status {} with description {} for current job id {}", status, description, id);
        return new Job<>(id, taskSchedule, started, task, status, description);
    }
}
