package org.jobs.manager.db;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.dao.JobDAO;
import org.jobs.manager.common.entities.EmailTask;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.stubs.Tasks;
import org.jobs.manager.entities.Task;
import org.jobs.manager.common.entities.TaskStatus;
import org.jobs.manager.common.schedulers.OnDateScheduler;
import org.jobs.manager.common.schedulers.Schedulers;
import org.jobs.manager.common.strategies.SendEmailTaskStrategyImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestDatabaseConfiguration.class})
class DataBaseJobApplicationTests {

    @Autowired
    private JobDAO jobDAO;

    private static EmailTask emailTask;

    private static OnDateScheduler scheduler;

    @BeforeAll
    static void uploadData() {
        Tuple2<EmailTask, OnDateScheduler> emailTestTask = Tasks.getEmailTestTask();
        emailTask = emailTestTask.getT1();
        scheduler = emailTestTask.getT2();
    }

    @Test
    void testSaveTask() {
        jobDAO.save(emailTask, scheduler);
        Job<Task> job = Job.queued(emailTask, scheduler);
        jobDAO.save(job);
        StepVerifier.create(jobDAO.getJobHistoryByTaskId(emailTask.getId()))
                .expectNextMatches(j ->
                        j.getId().equals(job.getId()))
                .verifyComplete();

        StepVerifier.create(jobDAO.getTask(emailTask.getId()))
                .expectNextMatches(t ->
                        t.getId().equals(emailTask.getId()))
                .verifyComplete();
    }

    @Test
    void testFreezeTask() {
        StepVerifier.create(jobDAO.takeJobs(1))
                .expectNextMatches(j -> j.getStatus() == TaskStatus.QUEUED)
                .verifyComplete();
        StepVerifier.create(jobDAO.takeJobs(1))
                .verifyComplete();
    }

    private static Tuple2<EmailTask, OnDateScheduler> getTestTask() {
        OnDateScheduler scheduler = Schedulers.getOnDateScheduler(UUID.randomUUID().toString(), LocalDateTime.now().minusSeconds(2), 0, true);
        EmailTask task = EmailTask.testBuilder()
                .id(UUID.randomUUID().toString())
                .strategyCode(SendEmailTaskStrategyImpl.SEND_EMAIL_STRATEGY_CODE)
                .subject("Urgent!")
                .body("Dear mr, ")
                .recipients(Arrays.asList("test2@mail.de", "test@mail.de"))
                .from("spam@mail.de")
                .build();
        return Tuples.of(task, scheduler);
    }
}

