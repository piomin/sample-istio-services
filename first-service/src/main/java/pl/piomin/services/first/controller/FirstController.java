package pl.piomin.services.first.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@RestController
@RequestMapping("/first")
public class FirstController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstController.class);

    Optional<BuildProperties> buildProperties;
    RestClient restClient;
    @Value("${VERSION}")
    private String version;

    public FirstController(Optional<BuildProperties> buildProperties, RestClient restClient) {
        this.buildProperties = buildProperties;
        this.restClient = restClient;
    }

    @GetMapping("/ping")
    public String ping() {
        LOGGER.info("Ping: name={}, version={}", buildProperties.isPresent() ? buildProperties.get().getName() : "first-service", version);
        String response = restClient.get().uri("http://caller-service:8080/caller/ping")
                .retrieve()
                .body(String.class);
        LOGGER.info("Calling: response={}", response);
        return "I'm first-service " + version + ". Calling... " + response;
    }

}
