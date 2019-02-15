package org.spring.jobs.manager.schedulers;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class OnDateJobSchedule implements JobSchedule {

    private static final long serialVersionUID = -7824113815420387857L;

    private final LocalDateTime startDate;

    public OnDateJobSchedule(@NonNull LocalDateTime startDate) {
        this.startDate = startDate;
    }
}
