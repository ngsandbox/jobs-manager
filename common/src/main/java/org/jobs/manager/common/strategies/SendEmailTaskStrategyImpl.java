package org.jobs.manager.common.strategies;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.entities.EmailTask;
import org.jobs.manager.common.shared.Task;
import org.jobs.manager.common.shared.TaskMetadata;
import org.jobs.manager.common.shared.TaskStrategy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

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
        return "Send email to the recipients";
    }

    @Override
    public TaskMetadata getTaskMetadata() {
        return new TaskMetadata(getCode(), getDescription(),
                Arrays.asList(EmailTask.FROM_CODE,
                        EmailTask.RECIPIENTS_CODE,
                        EmailTask.SUBJECT_CODE,
                        EmailTask.BODY_CODE));
    }

    @Override
    public Mono<Void> execute(Task task) {
        return Mono.empty();
    }
}
