package org.spring.jobs.manager;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spring.jobs.manager.schedulers.OnDateJobSchedule;
import org.spring.jobs.manager.strategies.SendEmailJobStrategyImpl;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class CommonJobManagerApplicationTests {

    private JobService jobService;

    @BeforeEach
    void initTest() {
        jobService = new JobService(2,
                Arrays.asList(new SendEmailJobStrategyImpl(), new TestJobStrategyImpl()));
    }

    @AfterEach
    void releaseResources() {
        jobService.close();
    }

    @Test
    void testNotReadyJob() {
        int TIMEOUT_SECS = 2;
        Job<TestJobDetail> job = getTestJob(TIMEOUT_SECS, 0, null, false);
        jobService.run(job)
                .subscribe(j -> checkJobStatus(j, JobStatus.QUEUED, "The job should not be ready yet"));
    }

    @Test
    void testSuccessJob() throws InterruptedException {
        int TIMEOUT_SECS = 2;
        Job<TestJobDetail> job = getTestJob(TIMEOUT_SECS, 0, null, false);
        Thread.sleep((TIMEOUT_SECS + 1) * 1000);
        jobService.run(job)
                .subscribe(j -> checkJobStatus(j, JobStatus.SUCCESS, "The job does not succeed at the appropriate time"));
    }

    @Test
    void testFailJob() throws InterruptedException {
        Job<TestJobDetail> job = getTestJob(0, 0, null, true);
        Thread.sleep(100);
        jobService.run(job)
                .subscribe(j -> checkJobStatus(j, JobStatus.FAILED, "The job does not succeed at the appropriate time"));
    }

    @Test
    void testParallelJobs() throws InterruptedException {
        int TIMEOUT_SECS = 0;
        List<Job> processedJobs = new CopyOnWriteArrayList<>();
        Job<TestJobDetail> job1 = getTestJob(TIMEOUT_SECS, 0, null, false);
        Job<TestJobDetail> job2 = getTestJob(TIMEOUT_SECS, 0, 4, false);
        Job<TestJobDetail> job3 = getTestJob(TIMEOUT_SECS, 0, 1, false);
        Thread.sleep(1000);
        Predicate<Job<TestJobDetail>> jobConsumer = (j) -> {
            log.info("Check job completion {}", j);
            processedJobs.add(j);
            return j.getStatus() == JobStatus.SUCCESS;
        };
        Flux<Job<TestJobDetail>> jobsFlux = Flux.merge(
                jobService.run(job1),
                jobService.run(job2),
                jobService.run(job3)
        );

        StepVerifier.create(jobsFlux)
                .expectNextMatches(jobConsumer)
                .expectNextMatches(jobConsumer)
                .expectNextMatches(jobConsumer)
                .expectComplete()
                .verify();
        Assertions.assertEquals(processedJobs.size(), 3, "Not all tasks completed");
        assertThat("Not all job was found", processedJobs, Matchers.containsInAnyOrder(job1, job2, job3));
    }

    private void checkJobStatus(Job<TestJobDetail> j, JobStatus success, String s) {
        log.info("Received job {}", j);
        Assertions.assertEquals(j.getStatus(), success, s);
    }

    private Job<TestJobDetail> getTestJob(int shiftSecs, int priority, Integer timeout, boolean throwError) {
        TestJobDetail jobDetail = TestJobDetail.builder()
                .id(UUID.randomUUID().toString())
                .strategyCode(TestJobStrategyImpl.TEST_STRATEGY_CODE)
                .schedule(new OnDateJobSchedule(LocalDateTime.now().plusSeconds(shiftSecs)))
                .created(LocalDateTime.now())
                .priority(priority)
                .timeoutSecs(timeout)
                .throwError(throwError)
                .build();
        return Job.queued(jobDetail);
    }
}

