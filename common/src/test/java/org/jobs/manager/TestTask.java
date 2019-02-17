package org.jobs.manager;

import lombok.*;
import org.jobs.manager.entities.Task;
import org.jobs.manager.schedulers.Scheduler;

import java.time.LocalDateTime;
import java.util.Collections;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TestTask extends Task {

    private static final long serialVersionUID = 4513958683692007945L;

    private final Integer timeoutSecs;

    private final boolean throwError;

    @Builder
    public TestTask(@NonNull String id,
                    @NonNull String strategyCode,
                    Integer timeoutSecs,
                    boolean throwError) {
        super(id, strategyCode, Collections.emptyMap());
        this.timeoutSecs = timeoutSecs;
        this.throwError = throwError;
    }
}
