package pl.piomin.services.callme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = "VERSION=v1")
public class CallmeControllerTests {

    RestTestClient restTestClient;

    @BeforeEach
    public void setUp(WebApplicationContext context) {
        restTestClient = RestTestClient.bindToApplicationContext(context)
                .baseUrl("/callme")
                .build();
    }

    @Test
    void ping() {
        restTestClient.get().uri("/ping")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("I'm callme-service v1");
    }

    @Test
    void pingWithRandomDelay() {
        restTestClient.get().uri("/ping-with-random-delay")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("I'm callme-service v1");
    }
}
