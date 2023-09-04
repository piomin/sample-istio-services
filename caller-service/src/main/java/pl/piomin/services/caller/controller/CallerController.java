package pl.piomin.services.caller.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/caller")
public class CallerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallerController.class);

    @Autowired
    Optional<BuildProperties> buildProperties;
    @Autowired
    RestTemplate restTemplate;
    @Value("${VERSION}")
    private String version;

    @GetMapping("/ping")
    public String ping() {
        LOGGER.info("Ping: name={}, version={}", buildProperties.or(Optional::empty), version);
        String response = restTemplate.getForObject("http://callme-service:8080/callme/ping", String.class);
        LOGGER.info("Calling: response={}", response);
        return "I'm caller-service " + version + ". Calling... " + response;
    }

    @GetMapping("/ping-with-random-error")
    public ResponseEntity<String> pingWithRandomError() {
        LOGGER.info("Ping with random error: name={}, version={}", buildProperties.or(Optional::empty), version);
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("http://callme-service:8080/callme/ping-with-random-error", String.class);
        LOGGER.info("Calling: responseCode={}, response={}", responseEntity.getStatusCode(), responseEntity.getBody());
        return new ResponseEntity<>("I'm caller-service " + version + ". Calling... " +
                responseEntity.getBody(), responseEntity.getStatusCode());
    }

    @GetMapping("/ping-with-random-delay")
    public String pingWithRandomDelay() {
        LOGGER.info("Ping with random delay: name={}, version={}", buildProperties.or(Optional::empty), version);
        String response = restTemplate
                .getForObject("http://callme-service:8080/callme/ping-with-random-delay", String.class);
        LOGGER.info("Calling: response={}", response);
        return "I'm caller-service " + version + ". Calling... " + response;
    }

}
