package org.jobs.manager.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.schedulers.Scheduler;

import java.io.Serializable;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class TaskSchedule implements Serializable {

    private static final long serialVersionUID = -2644276288742768039L;

    /**
     * Uniquie schedule task identifier
     */
    private final String id;

    /**
     * Uniquie task identifier
     */
    private final String taskId;

    /**
     * Priority MAX_VALUE - highest, MIN_VALUE - lowest
     */
    private final int priority;


    @NonNull
    @Setter
    private Scheduler schedule;

    @Builder
    public TaskSchedule(@NonNull String id,
                        @NonNull String taskId,
                        int priority,
                        @NonNull Scheduler schedule) {
        this.id = id;
        this.taskId = taskId;
        this.priority = priority;
        this.schedule = schedule;
    }
}
