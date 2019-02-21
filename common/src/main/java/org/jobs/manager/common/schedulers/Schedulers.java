package org.jobs.manager.common.schedulers;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public final class Schedulers {

    @Builder
    public static Optional<? extends Scheduler> getScheduler(String id,
                                                             String code,
                                                             String expression,
                                                             int priority,
                                                             boolean active) {
        log.debug("Try to find scheduler by code {} with id {} and expression {}", code, id, expression);
        if (OnDateScheduler.ON_DATE_SCHEDULER_CODE.equalsIgnoreCase(code)) {
            return Optional.of(getOnDateScheduler(id, LocalDateTime.parse(expression), priority, active));
        }

        if (CronScheduler.CRON_SCHEDULER_CODE.equalsIgnoreCase(code)) {
            return Optional.of(getCronScheduler(id, expression, priority, active));
        }

        return Optional.empty();
    }

    public static CronScheduler getCronScheduler(@NonNull String id, @NonNull String expression, int priority, boolean active) {
        return new CronScheduler(id, expression, priority, active);
    }

    public static OnDateScheduler getOnDateScheduler(@NonNull String id, @NonNull LocalDateTime dateTime, int priority, boolean active) {
        return new OnDateScheduler(id, dateTime, priority, active);
    }
}
