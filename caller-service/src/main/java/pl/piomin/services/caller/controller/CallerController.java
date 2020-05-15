package pl.piomin.services.caller.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/caller")
public class CallerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CallerController.class);
	
	@Autowired
	BuildProperties buildProperties;
	@Autowired
	RestTemplate restTemplate;
	@Value("${VERSION}")
	private String version;
	
	@GetMapping("/ping")
	public String ping() {
		LOGGER.info("Ping: name={}, version={}", buildProperties.getName(), version);
		String response = restTemplate.getForObject("http://callme-service:8080/callme/ping", String.class);
		LOGGER.info("Calling: response={}", response);
		return "I'm caller-service " + version + ". Calling... " + response;
	}
	
}
