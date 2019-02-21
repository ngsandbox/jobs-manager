package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.JobException;
import org.jobs.manager.common.schedulers.Scheduler;
import org.jobs.manager.common.schedulers.Schedulers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Table
public class ScheduleEntity implements Serializable {

    private static final long serialVersionUID = 3885492122269688915L;

    @Id
    @Column(name = "scheduleId")
    private String scheduleId;

    @Column(name = "taskId", insertable = false, updatable = false)
    private String taskId;

    @Column(name = "code")
    private String code;

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
        entity.code = scheduler.getCode();
        entity.expression = scheduler.getExpression();
        entity.startDate = scheduler.getStartDate();
        entity.priority = scheduler.getPriority();
        entity.active = scheduler.isActive();
        return entity;
    }

    public Scheduler toTaskScheduler() {
        Optional<? extends Scheduler> build = Schedulers.builder()
                .id(scheduleId)
                .code(getCode())
                .expression(getExpression())
                .priority(getPriority())
                .active(active)
                .build();
        return build
                .orElseThrow(() -> new JobException("Scheduler not found by code " + getCode()));
    }
}
