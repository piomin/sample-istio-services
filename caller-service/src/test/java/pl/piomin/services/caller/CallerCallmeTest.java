package pl.piomin.services.caller;

import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit5.HoverflyExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {"VERSION = v2"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(HoverflyExtension.class)
public class CallerCallmeTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void callmeIntegration(Hoverfly hoverfly) {
        hoverfly.simulate(
            dsl(service("http://callme-service:8080")
                .get("/callme/ping")
                .willReturn(success().body("I'm callme-service v1.")))
        );
        String response = restTemplate.getForObject("/caller/ping", String.class);
        assertEquals("I'm caller-service v2. Calling... I'm callme-service v1.", response);
    }
}
