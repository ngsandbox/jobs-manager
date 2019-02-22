package org.jobs.manager.common.schedulers;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.jobs.manager.common.utils.DateUtils;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * @see CronSequenceGenerator
 */
@Getter
@ToString
public class CronScheduler implements Scheduler {

    private static final long serialVersionUID = -4939181090148653944L;

    public static final String CRON_SCHEDULER_CODE = "CRON_SCHEDULER";

    private final LocalDateTime startDate;
    private final String expression;
    private final CronSequenceGenerator generator;
    private final String id;
    private final int priority;
    private final boolean active;

    /**
     * * <p>Example patterns:
     * * <ul>
     * * <li>"0 0 * * * *" = the top of every hour of every day.</li>
     * * <li>"*&#47;10 * * * * *" = every ten seconds.</li>
     * * <li>"0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.</li>
     * * <li>"0 0 6,19 * * *" = 6:00 AM and 7:00 PM every day.</li>
     * * <li>"0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30, 10:00 and 10:30 every day.</li>
     * * <li>"0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays</li>
     * * <li>"0 0 0 25 12 ?" = every Christmas Day at midnight</li>
     * * </ul>
     */
    CronScheduler(String id, LocalDateTime startDate, @NonNull String pattern, int priority, boolean active) {
        this.id = id;
        this.expression = pattern;
        this.generator = new CronSequenceGenerator(pattern);
        this.active = active;
        this.startDate = startDate;
        this.priority = priority;
    }

    CronScheduler(String id, @NonNull String pattern, int priority, boolean active) {
        this.id = id;
        this.expression = pattern;
        this.generator = new CronSequenceGenerator(pattern);
        this.active = active;
        this.startDate = DateUtils.convertTo(generator.next(new Date()));
        this.priority = priority;
    }

    @Override
    public String getCode() {
        return CRON_SCHEDULER_CODE;
    }

    /**
     * Check the cron next date and create new active scheduler
     */
    @Override
    public Optional<Scheduler> next() {
        Date next = generator.next(new Date());
        if (next.after(new Date())) {
            return Optional.of(new CronScheduler(id, expression, priority, true));
        }

        return Optional.empty();
    }
}
