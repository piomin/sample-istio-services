package pl.piomin.services.callme;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = "VERSION=v1")
public class CallmeControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void ping() {
        String res = restTemplate.getForObject("/callme/ping", String.class);
        Assertions.assertNotNull(res);
        Assertions.assertEquals("I'm callme-service v1", res);
    }

    @Test
    void pingWithRandomDelay() {
        String res = restTemplate.getForObject("/callme/ping-with-random-delay", String.class);
        Assertions.assertNotNull(res);
        Assertions.assertEquals("I'm callme-service v1", res);
    }
}
