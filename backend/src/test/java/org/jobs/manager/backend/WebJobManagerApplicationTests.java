package org.jobs.manager.backend;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.backend.models.SchedulerModel;
import org.jobs.manager.backend.models.TaskInfo;
import org.jobs.manager.backend.models.TaskModel;
import org.jobs.manager.common.entities.EmailTask;
import org.jobs.manager.common.schedulers.CronScheduler;
import org.jobs.manager.common.schedulers.OnDateScheduler;
import org.jobs.manager.common.stubs.Tasks;
import org.jobs.manager.common.stubs.TestTaskStrategyImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestBackendConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
class WebJobManagerApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void callTasksList() {
        webTestClient
                .get().uri("/v1/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // and use the dedicated DSL to test assertions against the response
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    void registerTask() {
        Tuple2<EmailTask, OnDateScheduler> emailTestTask = Tasks.getEmailTestTask();
        TaskInfo taskInfo = new TaskInfo(TaskModel.toModel(emailTestTask.getT1()),
                SchedulerModel.toModel(emailTestTask.getT2()));
        webTestClient
                .post()
                .uri("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(taskInfo))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

        webTestClient
                .get()
                .uri("/v1/tasks/{taskId}", taskInfo.getTask().getId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskInfo.class);
    }

    @Test
    public void registerCronTask() {
        CronScheduler cronScheduler = Tasks.getCronScheduler("*/2 * * * * *", 0);
        EmailTask emailTask = Tasks.getEmailTask(TestTaskStrategyImpl.TEST_STRATEGY_CODE);
        TaskInfo taskInfo = new TaskInfo(TaskModel.toModel(emailTask),
                SchedulerModel.toModel(cronScheduler));
        webTestClient
                .post()
                .uri("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(taskInfo))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
        Flux<String> jobFlux = webTestClient
                .get()
                .uri("/v1/jobs/listen")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .log();
//        String s = jobFlux.blockFirst(Duration.ofMillis(10000));
//        log.debug("Received {}", s);
//        StepVerifier.create(jobFlux)
//                .expectNextMatches(Objects::nonNull)
//                .thenCancel()
//                .verify(Duration.ofMillis(4500));
    }
}

