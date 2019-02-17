package org.jobs.manager.stubs;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.JobException;
import org.jobs.manager.TestTask;
import org.jobs.manager.strategies.TaskStrategy;
import org.springframework.stereotype.Component;

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
    public void execute(TestTask task) {
        log.info("Run test task {}", task);
        try {
            if (task.getTimeoutSecs() != null)
                Thread.sleep(task.getTimeoutSecs() * 1000);
        } catch (InterruptedException e) {
            log.error("Thread was interrupted", e);
            throw new JobException("Thread fault", e);
        }

        if(task.isThrowError()){
            throw new JobException("Test exception for task: " + task.getId());
        }

        log.info("Finish test job {}", task);
    }
}
