package org.jobs.manager.backend;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

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
    public void postTasksList() {
        webTestClient
                .post().body().uri("/v1/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // and use the dedicated DSL to test assertions against the response
                .expectStatus().isOk()
                .expectBody(Void.class);
    }
}

