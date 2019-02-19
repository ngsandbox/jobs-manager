package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.JobException;
import org.jobs.manager.schedulers.Scheduler;
import org.jobs.manager.schedulers.Schedulers;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;


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
    private String id;

    private String code;

    private String expression;

    private LocalDateTime startDate;

    private int priority;

    private boolean active;

    static ScheduleEntity from(@NonNull Scheduler scheduler) {
        ScheduleEntity nested = new ScheduleEntity();
        nested.setCode(scheduler.getCode());
        nested.setExpression(scheduler.getExpression());
        nested.setPriority(scheduler.getPriority());
        return nested;
    }

    Scheduler toTaskScheduler() {
        return Schedulers
                .getScheduler(getCode(), getExpression(), getPriority())
                .orElseThrow(() -> new JobException("Scheduler not found by code " + getCode()));
    }
}
