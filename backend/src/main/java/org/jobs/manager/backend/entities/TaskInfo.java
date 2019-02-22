package org.jobs.manager.backend.entities;


import lombok.*;
import org.jobs.manager.common.schedulers.Scheduler;
import org.jobs.manager.entities.Task;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class TaskInfo {
    private Task task;
    private Scheduler scheduler;

    @Builder
    public TaskInfo(Task task, Scheduler scheduler) {
        this.task = task;
        this.scheduler = scheduler;
    }
}
