package org.jobs.manager.backend.models;


import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.schedulers.Scheduler;
import org.jobs.manager.common.shared.Task;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class TaskInfo implements Serializable {

    private static final long serialVersionUID = -6694033318594928059L;

    @Valid
    @NotNull(message = "Please provide task info")
    private TaskModel task;

    @Valid
    @NotNull(message = "Please provide schedule info")
    private SchedulerModel scheduler;

    public TaskInfo(TaskModel task, SchedulerModel scheduler) {
        this.task = task;
        this.scheduler = scheduler;
    }

    public static TaskInfo from(Task task) {
        return from(task, null);
    }
    public static TaskInfo from(Task task, Scheduler scheduler) {
        return new TaskInfo(TaskModel.toModel(task), SchedulerModel.toModel(scheduler));
    }
}
