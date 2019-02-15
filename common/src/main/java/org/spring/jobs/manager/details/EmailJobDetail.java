package org.spring.jobs.manager.details;

import lombok.*;
import org.spring.jobs.manager.schedulers.JobSchedule;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EmailJobDetail extends JobDetail {

    private final String from;
    private final List<String> recipients;
    private final String subject;
    private final String body;

    @Builder
    public EmailJobDetail(String id,
                          String strategyCode,
                          JobSchedule schedule,
                          LocalDateTime started,
                          int priority,
                          @NonNull String from,
                          @NonNull List<String> recipients,
                          @NonNull String subject,
                          @NonNull String body) {
        super(id, strategyCode, schedule, started, priority);
        this.from = from;
        this.recipients = Collections.unmodifiableList(recipients);
        this.subject = subject;
        this.body = body;
    }
}
