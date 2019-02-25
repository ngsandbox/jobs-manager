package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.JobException;
import org.jobs.manager.common.schedulers.Scheduler;
import org.jobs.manager.common.schedulers.Schedulers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;


@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ScheduleEntity implements Serializable {

    private static final long serialVersionUID = 3885492122269688915L;

    @Id
    @Column(name = "scheduleId")
    private String scheduleId;

    @Column(name = "taskId", insertable = false, updatable = false)
    private String taskId;

    @Column(name = "schedulerCode")
    private String schedulerCode;

    @Column(name = "expression")
    private String expression;

    @Column(name = "startDate")
    private LocalDateTime startDate;

    @Column(name = "priority")
    private int priority;

    @Column(name = "active")
    private boolean active;

    public static ScheduleEntity from(@NonNull String taskId, @NonNull Scheduler scheduler) {
        ScheduleEntity entity = new ScheduleEntity();
        entity.scheduleId = scheduler.getId();
        entity.taskId = taskId;
        entity.schedulerCode = scheduler.getCode();
        entity.expression = scheduler.getExpression();
        entity.startDate = scheduler.getStartDate();
        entity.priority = scheduler.getPriority();
        entity.active = scheduler.isActive();
        return entity;
    }

    Scheduler toTaskScheduler() {
        Optional<? extends Scheduler> scheduler = Schedulers.builder()
                .id(scheduleId)
                .schedulerCode(schedulerCode)
                .expression(expression)
                .startDate(startDate)
                .priority(priority)
                .active(active)
                .build();
        return scheduler
                .orElseThrow(() -> new JobException("Scheduler not found by schedulerCode " + schedulerCode));
    }
}
