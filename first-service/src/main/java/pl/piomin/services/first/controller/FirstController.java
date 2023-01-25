package pl.piomin.services.first.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/first")
public class FirstController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstController.class);

    @Autowired
    Optional<BuildProperties> buildProperties;
    @Autowired
    RestTemplate restTemplate;
    @Value("${VERSION}")
    private String version;

    @GetMapping("/ping")
    public String ping() {
        LOGGER.info("Ping: name={}, version={}", buildProperties.isPresent() ? buildProperties.get().getName() : "first-service", version);
        String response = restTemplate.getForObject("http://caller-service:8080/caller/ping", String.class);
        LOGGER.info("Calling: response={}", response);
        return "I'm first-service " + version + ". Calling... " + response;
    }

}
