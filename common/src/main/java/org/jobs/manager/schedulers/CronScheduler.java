package org.jobs.manager.schedulers;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.jobs.manager.utils.DateUtils;
import org.springframework.scheduling.support.CronSequenceGenerator;

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

    private final LocalDateTime startDate;
    private final String cronExpression;
    private final CronSequenceGenerator generator;

    /**
     *  * <p>Example patterns:
     *  * <ul>
     *  * <li>"0 0 * * * *" = the top of every hour of every day.</li>
     *  * <li>"*&#47;10 * * * * *" = every ten seconds.</li>
     *  * <li>"0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.</li>
     *  * <li>"0 0 6,19 * * *" = 6:00 AM and 7:00 PM every day.</li>
     *  * <li>"0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30, 10:00 and 10:30 every day.</li>
     *  * <li>"0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays</li>
     *  * <li>"0 0 0 25 12 ?" = every Christmas Day at midnight</li>
     *  * </ul>
     */
    public CronScheduler(@NonNull String pattern) {
        this.cronExpression = pattern;
        this.generator = new CronSequenceGenerator(pattern);
        this.startDate = DateUtils.convertTo(generator.next(new Date()));
    }

    @Override
    public Optional<Scheduler> next() {
        Date next = generator.next(new Date());
        if (next.after(new Date())) {
            return Optional.of(new CronScheduler(cronExpression));
        }

        return Optional.empty();
    }
}
