package org.spring.jobs.manager;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.spring.jobs.manager.details.EmailJobDetail;
import org.spring.jobs.manager.details.JobDetail;
import org.spring.jobs.manager.strategies.BaseJobStrategy;

@ToString(callSuper = true)
@Slf4j
public class TestJobStrategyImpl extends BaseJobStrategy<TestJobDetail> {
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
    protected void execute(TestJobDetail jobDetail) {
        log.info("Run test job {}", jobDetail);
        try {
            if (jobDetail.getTimeoutSecs() != null)
                Thread.sleep(jobDetail.getTimeoutSecs() * 1000);
        } catch (InterruptedException e) {
            log.error("Thread was interrupted", e);
            throw new JobException("Thread fault", e);
        }

        if(jobDetail.isThrowError()){
            throw new JobException("Test exception for jobDetail: " + jobDetail.getId());
        }

        log.info("Finish test job {}", jobDetail);
    }
}
