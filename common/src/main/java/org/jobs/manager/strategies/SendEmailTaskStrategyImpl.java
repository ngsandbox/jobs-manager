package org.jobs.manager.strategies;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.entities.EmailTask;
import org.springframework.stereotype.Component;

@Component
@ToString(callSuper = true)
@Slf4j
public class SendEmailTaskStrategyImpl implements TaskStrategy<EmailTask> {
    @Override
    public String getCode() {
        return "SEND_EMAIL";
    }

    @Override
    public String getDescription() {
        return "Sending emails to the provided consumers";
    }

    @Override
    public void execute(EmailTask task) {
    }
}
