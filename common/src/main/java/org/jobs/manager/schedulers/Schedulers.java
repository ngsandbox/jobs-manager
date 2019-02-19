package org.jobs.manager.schedulers;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public final class Schedulers {

    public static Optional<? extends Scheduler> getScheduler(String code, String expression, int priority) {
        log.debug("Try to find scheduler by code {} with expression {}", code, expression);
        if (OnDateScheduler.ON_DATE_SCHEDULER_CODE.equalsIgnoreCase(code)) {
            return Optional.of(getOnDateScheduler(LocalDateTime.parse(expression), priority));
        }

        if (CronScheduler.CRON_SCHEDULER_CODE.equalsIgnoreCase(code)) {
            return Optional.of(getCronScheduler(expression, priority));
        }

        return Optional.empty();
    }

    public static CronScheduler getCronScheduler(@NonNull String expression, int priority){
        return new CronScheduler(expression, priority);
    }

    public static OnDateScheduler getOnDateScheduler(@NonNull LocalDateTime dateTime, int priority){
        return new OnDateScheduler(dateTime, priority);
    }
}
