package org.jobs.manager.common.schedulers;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.unmodifiableList;

@Slf4j
public final class Schedulers {

    public final static List<String> SCHEDULERS = unmodifiableList(Arrays.asList(OnDateScheduler.ON_DATE_SCHEDULER_CODE, CronScheduler.CRON_SCHEDULER_CODE));

    public static boolean doesExist(String schedulerCode) {
        log.debug("Try to find scheduler by schedulerCode {}", schedulerCode);
        return SCHEDULERS.stream().anyMatch(p -> p.equalsIgnoreCase(schedulerCode));
    }

    public static CronScheduler getCronScheduler(@NonNull String id, @NonNull String expression, int priority, boolean active) {
        return new CronScheduler(id, expression, priority, active);
    }

    private static CronScheduler getCronScheduler(@NonNull String id, @NonNull String expression, LocalDateTime startDate, int priority, boolean active) {
        return startDate == null ?
                getCronScheduler(id, expression, priority, active) :
                new CronScheduler(id, startDate, expression, priority, active);
    }

    public static OnDateScheduler getOnDateScheduler(@NonNull String id, @NonNull LocalDateTime dateTime, int priority, boolean active) {
        return new OnDateScheduler(id, dateTime, priority, active);
    }

    /**
     * Find scheduler type by schedulerCode and build new
     *
     * @param schedulerCode scheduler type {@see CronScheduler}
     * @param id           uniquer identifier of the scheduler's instance
     * @param expression   string expression for scheduler
     * @param priority     MAX_VALUE - highest, MIN_VALUE - lowest
     * @param active       does is active or not
     * @return return new instance if scheduler was found
     */
    @Builder
    public static Optional<? extends Scheduler> from(String id, @NonNull String schedulerCode, @NonNull String expression, LocalDateTime startDate, int priority, boolean active) {
        log.debug("Try to find scheduler by schedulerCode {} with id {} and expression {}", schedulerCode, id, expression);
        if (OnDateScheduler.ON_DATE_SCHEDULER_CODE.equalsIgnoreCase(schedulerCode)) {
            return Optional.of(getOnDateScheduler(id, LocalDateTime.parse(expression), priority, active));
        }

        if (CronScheduler.CRON_SCHEDULER_CODE.equalsIgnoreCase(schedulerCode)) {
            return Optional.of(getCronScheduler(id, expression, startDate, priority, active));
        }

        return Optional.empty();
    }
}
