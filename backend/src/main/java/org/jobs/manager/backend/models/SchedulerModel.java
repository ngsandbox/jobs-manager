package org.jobs.manager.backend.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.backend.BackendException;
import org.jobs.manager.common.schedulers.Scheduler;
import org.jobs.manager.common.schedulers.Schedulers;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SchedulerModel implements Serializable {

    private static final long serialVersionUID = -4161299462459297523L;

    @NotNull(message = "Scheduler code should be specified")
    private String schedulerCode;

    @NotNull(message = "Please provide scheduler expression")
    private String expression;

    @NotNull(message = "Please provide priority of the task")
    private Integer priority;

    private Boolean active;


    private SchedulerModel(String strategyCode,
                           String expression,
                           int priority,
                           boolean active) {
        this.active = active;
        this.schedulerCode = strategyCode;
        this.expression = expression;
        this.priority = priority;
    }

    public Scheduler takeScheduler() {
        log.debug("Convert from transport scheduler {}", this);
        Optional<? extends Scheduler> scheduler = Schedulers.builder()
                .schedulerCode(schedulerCode)
                .id(UUID.randomUUID().toString())
                .expression(expression)
                .priority(priority)
                .active(active != null ? active : true)
                .build();
        return scheduler.orElseThrow(() -> new BackendException("Could not create strategy with schedulerCode: " + schedulerCode));
    }

    public static SchedulerModel toModel(Scheduler scheduler) {
        log.debug("Convert to transport scheduler {}", scheduler);
        if (scheduler == null) {
            return null;
        }

        return new SchedulerModel(scheduler.getCode(),
                scheduler.getExpression(),
                scheduler.getPriority(),
                scheduler.isActive());
    }
}
