package org.jobs.manager.backend.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.entities.TaskStatus;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@ToString
public class JobInfo {
    private String id;

    private TaskModel task;

    private LocalDateTime started;

    private TaskStatus status;

    private String description;

    private SchedulerModel scheduler;

    public static JobInfo from(Job job) {
        if (job == null) {
            return null;
        }

        JobInfo info = new JobInfo();
        info.id = job.getId();
        info.task = TaskModel.toModel(job.getTask());
        info.started = job.getStarted();
        info.status = job.getStatus();
        info.description = job.getDescription();
        info.scheduler = SchedulerModel.toModel(job.getScheduler());
        return info;
    }
}
