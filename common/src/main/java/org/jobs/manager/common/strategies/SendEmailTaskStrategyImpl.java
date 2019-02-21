package org.jobs.manager.common.strategies;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.entities.EmailTask;
import org.jobs.manager.strategies.TaskStrategy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@ToString(callSuper = true)
@Slf4j
public class SendEmailTaskStrategyImpl implements TaskStrategy<EmailTask> {

    public static final String SEND_EMAIL_STRATEGY_CODE = "SEND_EMAIL";

    @Override
    public String getCode() {
        return SEND_EMAIL_STRATEGY_CODE;
    }

    @Override
    public String getDescription() {
        return "Sending emails to the provided consumers";
    }

    @Override
    public Mono<Void> execute(EmailTask task) {
        return Mono.empty();
    }
}
