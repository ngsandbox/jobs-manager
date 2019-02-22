package org.jobs.manager.common.stubs;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.JobException;
import org.jobs.manager.common.shared.Task;
import org.jobs.manager.common.shared.TaskStrategy;
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
    public Mono<Void> execute(Task task) {
        log.info("Run test task {}", task);
        TestTask testTask = TestTask.of(task);
        if (testTask.isThrowError()) {
            return Mono.error(new JobException("Test exception for task: " + task.getId()));
        }

        if (testTask.getTimeoutSecs() != null)
            return Mono.delay(Duration.ofSeconds(testTask.getTimeoutSecs()))
                    .flatMap(aLong -> Mono.empty());

        log.info("Finish test job {}", task);
        return Mono.empty();
    }
}
