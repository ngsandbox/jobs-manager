package org.jobs.manager.common.stubs;

import org.jobs.manager.common.entities.EmailTask;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.schedulers.OnDateScheduler;
import org.jobs.manager.common.schedulers.Schedulers;
import org.jobs.manager.common.strategies.SendEmailTaskStrategyImpl;
import org.jobs.manager.entities.Task;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

public class Tasks {

    public static Job<Task> getCronTestJob(String pattern, int priority, Integer timeout, boolean throwError) {
        TestTask task = TestTask.testBuilder()
                .id(UUID.randomUUID().toString())
                .strategyCode(TestTaskStrategyImpl.TEST_STRATEGY_CODE)
                .timeoutSecs(timeout)
                .throwError(throwError)
                .build();

        return Job.queued(task, Schedulers.getCronScheduler(UUID.randomUUID().toString(), pattern, priority, true));
    }


    public static Job<TestTask> getTestJob(int shiftSecs, int priority, Integer timeout, boolean throwError) {
        TestTask task = TestTask.testBuilder()
                .id(UUID.randomUUID().toString())
                .strategyCode(TestTaskStrategyImpl.TEST_STRATEGY_CODE)
                .timeoutSecs(timeout)
                .throwError(throwError)
                .build();
        OnDateScheduler onDateScheduler = Schedulers.getOnDateScheduler(UUID.randomUUID().toString(),
                LocalDateTime.now().plusSeconds(shiftSecs), priority, true);
        return Job.queued(task, onDateScheduler);
    }


    public static Tuple2<EmailTask, OnDateScheduler> getEmailTestTask() {
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
