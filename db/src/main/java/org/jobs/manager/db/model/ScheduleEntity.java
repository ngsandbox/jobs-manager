package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.jobs.manager.JobException;
import org.jobs.manager.schedulers.Scheduler;
import org.jobs.manager.schedulers.Schedulers;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "schedule")
@NamedEntityGraph(name = "ScheduleEntity.task", attributeNodes = @NamedAttributeNode("task"))
public class ScheduleEntity implements Serializable {

    private static final long serialVersionUID = 3885492122269688915L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String scheduleId;

//    @Column(name="taskId", insertable = false, updatable = false)
//    private String taskId;

    private String code;

    private String expression;

    private LocalDateTime startDate;

    private int priority;

    private boolean active;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "schedule")
    private TaskEntity task;

    static ScheduleEntity from(@NonNull String taskId, @NonNull Scheduler scheduler) {
        ScheduleEntity entity = new ScheduleEntity();
        if (StringUtils.isNotEmpty(scheduler.getId())) {
            entity.setScheduleId(scheduler.getId());
        }

//        entity.setTaskId(taskId);
        entity.setCode(scheduler.getCode());
        entity.setExpression(scheduler.getExpression());
        entity.setPriority(scheduler.getPriority());
        return entity;
    }

    Scheduler toTaskScheduler() {
        return Schedulers
                .getScheduler(scheduleId, getCode(), getExpression(), getPriority())
                .orElseThrow(() -> new JobException("Scheduler not found by code " + getCode()));
    }
}
