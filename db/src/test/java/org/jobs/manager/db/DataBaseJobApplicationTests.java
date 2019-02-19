package org.jobs.manager.db;

import org.jobs.manager.dao.JobDAO;
import org.jobs.manager.entities.EmailTask;
import org.jobs.manager.entities.Task;
import org.jobs.manager.schedulers.OnDateScheduler;
import org.jobs.manager.schedulers.Schedulers;
import org.jobs.manager.strategies.SendEmailTaskStrategyImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataBaseConfiguration.class)
public class DataBaseJobApplicationTests {

    @Autowired
    private JobDAO jobDAO;

    @Test
    public void testSaveTask() {
        OnDateScheduler scheduler = Schedulers.getOnDateScheduler(LocalDateTime.now(), 0);
        Task task = EmailTask.testBuilder()
                .id(UUID.randomUUID().toString())
                .strategyCode(SendEmailTaskStrategyImpl.SEND_EMAIL_STRATEGY_CODE)
                .subject("Urgent!")
                .body("Dear mr, ")
                .recipients(Arrays.asList("test2@mail.de", "test@mail.de"))
                .from("spam@mail.de")
                .build();
        StepVerifier.create(jobDAO.save(task, scheduler))
                .expectNextMatches(t -> t.getId().equals(task.getId()))
                .verifyComplete();
        StepVerifier.create(jobDAO.getTask(task.getId()))
                .expectNextMatches(t -> t.getId().equals(task.getId()))
                .verifyComplete();
    }

}

