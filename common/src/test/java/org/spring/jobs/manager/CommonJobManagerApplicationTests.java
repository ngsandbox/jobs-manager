package org.spring.jobs.manager;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spring.jobs.manager.details.JobDetail;
import org.spring.jobs.manager.schedulers.OnDateJobSchedule;
import org.spring.jobs.manager.strategies.SendEmailJobStrategyImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class CommonJobManagerApplicationTests {

    private JobService jobService;

    @BeforeEach
    void initTest() {
        jobService = new JobService(2,
                Arrays.asList(new SendEmailJobStrategyImpl(), new TestJobStrategyImpl()));
    }

    @Test
    void testNotReadyJob() {
        int TIMEOUT_SECS = 2;
        Job<TestJobDetail> job = getTestJob(TIMEOUT_SECS, 0, null, false);
        jobService.run(job).subscribe(j -> {
            log.info("Received job {}", j);
            Assertions.assertEquals(j.getStatus(), JobStatus.QUEUED, "The job should not be ready yet");
        });
    }

    @Test
    void testSuccessJob() throws InterruptedException {
        int TIMEOUT_SECS = 2;
        Job<TestJobDetail> job = getTestJob(TIMEOUT_SECS, 0, null, false);
        Thread.sleep((TIMEOUT_SECS + 1) * 1000);
        jobService.run(job).subscribe(j -> {
            log.info("Received job {}", j);
            Assertions.assertEquals(j.getStatus(), JobStatus.SUCCESS, "The job does not succeed at the appropriate time");
        });
    }

    private Job<TestJobDetail> getTestJob(int siftSecs, int priority, Integer timeout, boolean throwError) {
        TestJobDetail jobDetail = TestJobDetail.builder()
                .id(UUID.randomUUID().toString())
                .strategyCode(TestJobStrategyImpl.TEST_STRATEGY_CODE)
                .schedule(new OnDateJobSchedule(LocalDateTime.now().plusSeconds(siftSecs)))
                .created(LocalDateTime.now())
                .priority(priority)
                .timeoutSecs(timeout)
                .throwError(throwError)
                .build();
        return Job.queued(jobDetail);
    }
}

