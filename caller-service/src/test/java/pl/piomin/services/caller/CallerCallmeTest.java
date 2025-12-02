package pl.piomin.services.caller;

import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit5.HoverflyExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;

@SpringBootTest(properties = {"VERSION = v2"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(HoverflyExtension.class)
public class CallerCallmeTest {

    RestTestClient restTestClient;

    @BeforeEach
    public void setUp(WebApplicationContext context) {
        restTestClient = RestTestClient.bindToApplicationContext(context)
                .baseUrl("/caller")
                .build();
    }

    @Test
    public void callmeIntegration(Hoverfly hoverfly) {
        hoverfly.simulate(
            dsl(service("http://callme-service:8080")
                .get("/callme/ping")
                .willReturn(success().body("I'm callme-service v1.")))
        );
        restTestClient.get().uri("/ping")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("I'm caller-service v2. Calling... I'm callme-service v1.");
    }
}
