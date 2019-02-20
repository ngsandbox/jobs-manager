package org.jobs.manager.schedulers;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class OnDateScheduler implements Scheduler {

    private static final long serialVersionUID = -7824113815420387857L;

    public static final String ON_DATE_SCHEDULER_CODE = "ON_DATE_SCHEDULER";

    private final String id;

    private final LocalDateTime startDate;

    private final int priority;

    OnDateScheduler(@NonNull String id, @NonNull LocalDateTime startDate, int priority) {
        this.id = id;
        this.startDate = startDate;
        this.priority = priority;
    }

    @Override
    public String getCode() {
        return ON_DATE_SCHEDULER_CODE;
    }

    @Override
    public String getExpression() {
        return startDate.toString();
    }
}
