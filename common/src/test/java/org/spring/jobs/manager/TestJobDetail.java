package org.spring.jobs.manager;

import lombok.*;
import org.spring.jobs.manager.details.JobDetail;
import org.spring.jobs.manager.schedulers.JobSchedule;

import java.time.LocalDateTime;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TestJobDetail extends JobDetail {

    private final Integer timeoutSecs;
    private final boolean throwError;

    @Builder
    public TestJobDetail(@NonNull String id,
                         @NonNull String strategyCode,
                         @NonNull JobSchedule schedule,
                         @NonNull LocalDateTime created,
                         int priority,
                         Integer timeoutSecs,
                         boolean throwError) {
        super(id, strategyCode, schedule, created, priority);
        this.timeoutSecs = timeoutSecs;
        this.throwError = throwError;
    }
}
