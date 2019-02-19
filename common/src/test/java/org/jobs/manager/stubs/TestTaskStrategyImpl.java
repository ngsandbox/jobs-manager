package org.jobs.manager.stubs;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.JobException;
import org.jobs.manager.TestTask;
import org.jobs.manager.strategies.TaskStrategy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@ToString(callSuper = true)
@Slf4j
public class TestTaskStrategyImpl implements TaskStrategy<TestTask> {
    public static final String TEST_STRATEGY_CODE = "TEST_STRATEGY";

    @Override
    public String getCode() {
        return TEST_STRATEGY_CODE;
    }

    @Override
    public String getDescription() {
        return "Test strategy";
    }

    @Override
    public Mono<Void> execute(TestTask task) {
        log.info("Run test task {}", task);
        if (task.isThrowError()) {
            return Mono.error(new JobException("Test exception for task: " + task.getId()));
        }

        if (task.getTimeoutSecs() != null)
            return Mono.delay(Duration.ofSeconds(task.getTimeoutSecs()))
                    .flatMap(aLong -> Mono.empty());

        log.info("Finish test job {}", task);
        return Mono.empty();
    }
}
