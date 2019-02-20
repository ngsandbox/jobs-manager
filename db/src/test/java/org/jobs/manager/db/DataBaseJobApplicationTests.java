package org.jobs.manager.db;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.dao.JobDAO;
import org.jobs.manager.entities.EmailTask;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.schedulers.OnDateScheduler;
import org.jobs.manager.schedulers.Schedulers;
import org.jobs.manager.strategies.SendEmailTaskStrategyImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDatabaseConfiguration.class)
public class DataBaseJobApplicationTests {

    @Autowired
    private JobDAO jobDAO;

    @Test
    public void testSaveTask() {
        OnDateScheduler scheduler = Schedulers.getOnDateScheduler(UUID.randomUUID().toString(), LocalDateTime.now(), 0);
        EmailTask task = EmailTask.testBuilder()
                .id(UUID.randomUUID().toString())
                .strategyCode(SendEmailTaskStrategyImpl.SEND_EMAIL_STRATEGY_CODE)
                .subject("Urgent!")
                .body("Dear mr, ")
                .recipients(Arrays.asList("test2@mail.de", "test@mail.de"))
                .from("spam@mail.de")
                .build();
        jobDAO.save(task, scheduler);
        jobDAO.save(Job.queued(task, scheduler));
        jobDAO.getTaskHistory(task.getId())
                .subscribe(taskJob -> log.debug("History job{}", taskJob));
        StepVerifier.create(jobDAO.getTask(task.getId()))
                .expectNextMatches(t ->
                        t.getId().equals(task.getId()))
                .verifyComplete();
    }

}

