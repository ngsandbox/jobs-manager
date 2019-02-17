package org.jobs.manager;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.TaskSchedule;
import org.jobs.manager.entities.TaskStatus;
import org.jobs.manager.events.JobTopicEvent;
import org.jobs.manager.events.TopicService;
import org.jobs.manager.events.Topics;
import org.jobs.manager.schedulers.OnDateScheduler;
import org.jobs.manager.stubs.TestTaskStrategyImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("unchecked")
@SpringBootTest(classes = {TestConfiguration.class})
@Slf4j
public class JobExecutorApplicationTests {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobExecutor jobExecutor;

    @Autowired
    private TopicService topicService;

    @Test
    void testNotReadyJob() {
        int TIMEOUT_SECS = 2;
        Job<TestTask> job = getTestJob(TIMEOUT_SECS, 0, null, false);
        StepVerifier.create(jobExecutor.run(job))
                .expectComplete()
                .verify();
    }

    /**
     * Start cron job for every 5 seconds {@see #}
     * @see org.jobs.manager.stubs.TestJobsDAOImpl#cronTestTask
     */
    @Test
    void testCronJob() throws InterruptedException {
        List<Job<TestTask>> history = new CopyOnWriteArrayList<>();
        topicService.subscribe(Topics.JOB_TOPIC, topicEvent -> {
            Assertions.assertTrue(topicEvent instanceof JobTopicEvent, "The type of event wrong");
            history.add(((JobTopicEvent) topicEvent).getJob());
        });

        Thread.sleep(5_000);
        Assertions.assertFalse(history.isEmpty(), "History of cron task is empty");
        Assertions.assertEquals(4, history.size(), "History of cron task is empty");
    }

    @Test
    void testSuccessJob() throws InterruptedException {
        int TIMEOUT_SECS = 2;
        Job<TestTask> job = getTestJob(TIMEOUT_SECS, 0, null, false);
        Thread.sleep((TIMEOUT_SECS + 1) * 1000);
        StepVerifier.create(jobExecutor.run(job))
                .expectNextMatches(j -> validateJobStatus(j, TaskStatus.RUNNING, "The job does not run. " + j.getError()))
                .expectNextMatches(j -> validateJobStatus(j, TaskStatus.SUCCESS, "The job does not succeed at the appropriate time. " + j.getError()))
                .expectComplete()
                .verify();
    }

    @Test
    void testFailJobAndTopicSubscription() throws InterruptedException {
        AtomicReference<Job<TestTask>> ref = new AtomicReference<>();
        topicService.subscribe(Topics.JOB_TOPIC, topicEvent -> {
            Assertions.assertTrue(topicEvent instanceof JobTopicEvent, "The type of event wrong");
            ref.set(((JobTopicEvent) topicEvent).getJob());
        });
        Job<TestTask> job = getTestJob(0, 0, null, true);
        Thread.sleep(100);
        StepVerifier.create(jobExecutor.run(job))
                .expectNextMatches(j -> validateJobStatus(j, TaskStatus.FAILED, "The job does not succeed at the appropriate time. " + j.getError()))
                .expectComplete()
                .verify();
        Assertions.assertNotNull(ref.get());
    }

    @Test
    void testParallelJobs() throws InterruptedException {
        int TIMEOUT_SECS = 0;
        List<Job> processedJobs = new CopyOnWriteArrayList<>();
        Job<TestTask> job1 = getTestJob(TIMEOUT_SECS, 0, null, false);
        Job<TestTask> job2 = getTestJob(TIMEOUT_SECS, 0, 4, false);
        Job<TestTask> job3 = getTestJob(TIMEOUT_SECS, 0, 1, false);
        Thread.sleep(1000);
        Predicate<Job<TestTask>> jobConsumer = (j) -> {
            log.info("Check job completion {}", j);
            processedJobs.add(j);
            return j.getStatus() == TaskStatus.SUCCESS;
        };
        Flux<Job<TestTask>> jobsFlux = Flux.merge(
                jobExecutor.run(job1),
                jobExecutor.run(job2),
                jobExecutor.run(job3)
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

    private boolean validateJobStatus(Job<TestTask> j, TaskStatus success, String s) {
        log.info("Received job {}", j);
        Assertions.assertEquals(j.getStatus(), success, s);
        return true;
    }

    private Job<TestTask> getTestJob(int shiftSecs, int priority, Integer timeout, boolean throwError) {
        TestTask task = TestTask.builder()
                .id(UUID.randomUUID().toString())
                .strategyCode(TestTaskStrategyImpl.TEST_STRATEGY_CODE)
                .timeoutSecs(timeout)
                .throwError(throwError)
                .build();
        TaskSchedule taskSchedule = TaskSchedule.builder()
                .id(UUID.randomUUID().toString())
                .taskId(task.getId())
                .priority(priority)
                .schedule(new OnDateScheduler(LocalDateTime.now().plusSeconds(shiftSecs)))
                .build();
        return Job.queued(task, taskSchedule);
    }
}

