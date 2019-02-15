package org.spring.jobs.manager.details;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.spring.jobs.manager.schedulers.JobSchedule;

import java.io.Serializable;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class JobDetail implements Serializable {

    private static final long serialVersionUID = -2644276288742768039L;

    private final String id;

    private final String strategyCode;

    private final int priority;

    private final LocalDateTime created;

    private final JobSchedule schedule;

    public JobDetail(@NonNull String id,
              @NonNull String strategyCode,
              @NonNull JobSchedule schedule,
              @NonNull LocalDateTime created,
              int priority) {
        this.id = id;
        this.strategyCode = strategyCode;
        this.schedule = schedule;
        this.created = created;
        this.priority = priority;
    }
}
