package org.spring.jobs.manager.schedulers;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.spring.jobs.manager.utils.DateUtils;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Getter
@ToString
public class CronJobSchedule implements JobSchedule {

    private static final long serialVersionUID = -4939181090148653944L;

    private final LocalDateTime startDate;
    private final String cronExpression;
    private final CronSequenceGenerator generator;

    public CronJobSchedule(@NonNull String cronExpression) {
        this.cronExpression = cronExpression;
        this.generator = new CronSequenceGenerator(cronExpression);
        this.startDate = DateUtils.convertTo(generator.next(new Date()));
    }

    @Override
    public Optional<JobSchedule> next() {
        Date next = generator.next(new Date());
        if (next.after(new Date())) {
            return Optional.of(new CronJobSchedule(cronExpression));
        }

        return Optional.empty();
    }
}
