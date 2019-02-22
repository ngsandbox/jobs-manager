package org.jobs.manager.backend;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.backend.entities.TaskInfo;
import org.jobs.manager.common.entities.EmailTask;
import org.jobs.manager.common.schedulers.OnDateScheduler;
import org.jobs.manager.common.stubs.Tasks;
import org.jobs.manager.entities.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestBackendConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebJobManagerApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void callTasksList() {
        webTestClient
                .get().uri("/v1/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // and use the dedicated DSL to test assertions against the response
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void registerTask() {
        Tuple2<EmailTask, OnDateScheduler> emailTestTask = Tasks.getEmailTestTask();
        TaskInfo taskInfo = TaskInfo.builder()
                .task(emailTestTask.getT1())
                .scheduler(emailTestTask.getT2()).build();
        webTestClient
                .post()
                .uri("/v1/tasks")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                //.contentType(MediaType.APPLICATION_JSON_UTF8)
//                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(taskInfo))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }
}

