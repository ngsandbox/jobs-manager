package org.spring.jobs.manager.strategies;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.spring.jobs.manager.details.EmailJobDetail;

@ToString(callSuper = true)
@Slf4j
public class SendEmailJobStrategyImpl extends BaseJobStrategy<EmailJobDetail> {
    @Override
    public String getCode() {
        return "SEND_EMAIL";
    }

    @Override
    public String getDescription() {
        return "Sending emails to the provided consumers";
    }

    @Override
    protected void execute(EmailJobDetail jobDetail) {
    }
}
