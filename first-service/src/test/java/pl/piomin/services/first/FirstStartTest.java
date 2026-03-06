package pl.piomin.services.first;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"VERSION = v2"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FirstStartTest {

    @Test
    public void start() {

    }
}
