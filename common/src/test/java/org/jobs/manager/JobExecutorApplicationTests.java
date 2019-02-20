package org.jobs.manager;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.TaskStatus;
import org.jobs.manager.subscription.events.JobSubscriptionEvent;
import org.jobs.manager.subscription.events.SubscriptionEvent;
import org.jobs.manager.subscription.SubscriptionService;
import org.jobs.manager.subscription.Topics;
import org.jobs.manager.schedulers.OnDateScheduler;
import org.jobs.manager.schedulers.Schedulers;
import org.jobs.manager.stubs.TestTaskStrategyImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest(classes = {TestConfiguration.class})
@Slf4j
public class JobExecutorApplicationTests {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobExecutor jobExecutor;

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    void testNotReadyJob() {
        int TIMEOUT_SECS = 2;
        Job<TestTask> job = getTestJob(TIMEOUT_SECS, 0, null, false);
        StepVerifier.create(jobExecutor.run(job))
                .expectComplete()
                .verify();
    }

    /**
     * Start cron job for every 2 seconds {@see #}
     *
     * @see org.jobs.manager.stubs.TestJobsDAOImpl#cronTestTask
     */
    @Test
    void testCronJob() {
        Flux<SubscriptionEvent> flux = subscriptionService.subscribe(Topics.JOB_TOPIC);
        StepVerifier.create(flux)
                .expectNextMatches(subscriptionEvent -> subscriptionEvent instanceof JobSubscriptionEvent &&
                        validateJobStatus(((JobSubscriptionEvent) subscriptionEvent).getJob(), TaskStatus.RUNNING, "Job is not runned"))
                .expectNextMatches(subscriptionEvent -> subscriptionEvent instanceof JobSubscriptionEvent &&
                        validateJobStatus(((JobSubscriptionEvent) subscriptionEvent).getJob(), TaskStatus.SUCCESS, "Job does not succeed"))
                .expectNextMatches(subscriptionEvent -> subscriptionEvent instanceof JobSubscriptionEvent &&
                        validateJobStatus(((JobSubscriptionEvent) subscriptionEvent).getJob(), TaskStatus.RUNNING, "Job is not runned"))
                .expectNextMatches(subscriptionEvent -> subscriptionEvent instanceof JobSubscriptionEvent &&
                        validateJobStatus(((JobSubscriptionEvent) subscriptionEvent).getJob(), TaskStatus.SUCCESS, "Job does not succeed"))
                .thenCancel()
                .verify(Duration.ofMillis(4500));
    }

    @Test
    void testSuccessJob() throws InterruptedException {
        int TIMEOUT_SECS = 2;
        Job<TestTask> job = getTestJob(TIMEOUT_SECS, 0, null, false);
        Thread.sleep((TIMEOUT_SECS + 1) * 1000);
        StepVerifier.create(jobExecutor.run(job))
                .expectNextMatches(j -> validateJobStatus(j, TaskStatus.RUNNING, "The job does not run. " + j.getDescription()))
                .expectNextMatches(j -> validateJobStatus(j, TaskStatus.SUCCESS, "The job does not succeed at the appropriate time. " + j.getDescription()))
                .expectComplete()
                .verify();
    }

    @Test
    void testFailJob() {
        Job<TestTask> job = getTestJob(-1, 0, 0, true);
        StepVerifier.create(jobExecutor.run(job))
                .expectNextMatches(j -> validateJobStatus(j, TaskStatus.RUNNING, "The job does not run. " + j.getDescription()))
                .expectNextMatches(j -> validateJobStatus(j, TaskStatus.FAILED, "The job did not fail during execution. "))
                .expectComplete()
                .verify();
    }

    @Test
    void testParallelJobs() {
        int TIMEOUT_SECS = -1;
        Flux<Job<TestTask>> jobsFlux = Flux.concat(// use concat to save the order of the publishers
                jobExecutor.run(getTestJob(TIMEOUT_SECS, 0, null, false)),
                jobExecutor.run(getTestJob(TIMEOUT_SECS, 0, null, false)),
                jobExecutor.run(getTestJob(TIMEOUT_SECS, 0, null, false))
        );

        StepVerifier.create(jobsFlux)
                .thenAwait(Duration.ofMillis(100))
                .expectNextMatches(j -> j.getStatus() == TaskStatus.RUNNING)
                .expectNextMatches(j -> j.getStatus() == TaskStatus.SUCCESS)
                .expectNextMatches(j -> j.getStatus() == TaskStatus.RUNNING)
                .expectNextMatches(j -> j.getStatus() == TaskStatus.SUCCESS)
                .expectNextMatches(j -> j.getStatus() == TaskStatus.RUNNING)
                .expectNextMatches(j -> j.getStatus() == TaskStatus.SUCCESS)
                .expectComplete()
                .verify();
    }

    private boolean validateJobStatus(Job j, TaskStatus expected, String s) {
        log.info("Received job {}", j);
        Assertions.assertEquals(expected, j.getStatus(), s);
        return true;
    }

    private Job<TestTask> getTestJob(int shiftSecs, int priority, Integer timeout, boolean throwError) {
        TestTask task = TestTask.testBuilder()
                .id(UUID.randomUUID().toString())
                .strategyCode(TestTaskStrategyImpl.TEST_STRATEGY_CODE)
                .timeoutSecs(timeout)
                .throwError(throwError)
                .build();
        OnDateScheduler onDateScheduler = Schedulers.getOnDateScheduler(UUID.randomUUID().toString(),
                LocalDateTime.now().plusSeconds(shiftSecs), priority);
        return Job.queued(task, onDateScheduler);
    }
}

