package org.jobs.manager.schedulers;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class OnDateScheduler implements Scheduler {

    private static final long serialVersionUID = -7824113815420387857L;

    private final LocalDateTime startDate;

    public OnDateScheduler(@NonNull LocalDateTime startDate) {
        this.startDate = startDate;
    }
}
