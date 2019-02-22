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
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SchedulerModel {

    private String strategyCode;

    @NotNull
    private String expression;

    private int priority;

    private boolean active;


    private SchedulerModel(String strategyCode,
                          String expression,
                          int priority,
                          boolean active) {
        this.active = active;
        this.strategyCode = strategyCode;
        this.expression = expression;
        this.priority = priority;
    }

    public Scheduler takeScheduler() {
        log.debug("Convert from transport scheduler {}", this);
        Optional<? extends Scheduler> scheduler = Schedulers.builder()
                .strategyCode(strategyCode)
                .id(UUID.randomUUID().toString())
                .expression(expression)
                .priority(priority)
                .active(active)
                .build();
        return scheduler.orElseThrow(() -> new BackendException("Could not create strategy with strategyCode: " + strategyCode));
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
