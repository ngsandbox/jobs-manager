package org.jobs.manager.db;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.dao.JobDAO;
import org.jobs.manager.common.entities.EmailTask;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.entities.TaskStatus;
import org.jobs.manager.common.schedulers.OnDateScheduler;
import org.jobs.manager.common.stubs.Tasks;
import org.jobs.manager.common.shared.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestDatabaseConfiguration.class})
class DataBaseJobApplicationTests {

    @Autowired
    private JobDAO jobDAO;

    private EmailTask emailTask;

    private OnDateScheduler scheduler;

    @BeforeEach
    void uploadData() {
        Tuple2<EmailTask, OnDateScheduler> emailTestTask = Tasks.getEmailTestTask();
        emailTask = emailTestTask.getT1();
        scheduler = emailTestTask.getT2();
        jobDAO.save(emailTask, scheduler);
    }

    @Test
    void testSaveTask() {
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
                .expectNextMatches(j -> j.getStatus() == TaskStatus.QUEUED)
                .verifyComplete();
    }
}

