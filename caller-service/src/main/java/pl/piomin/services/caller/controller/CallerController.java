package pl.piomin.services.caller.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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

	@GetMapping("/ping")
	public String ping(@RequestHeader(name = "x-version", required = false) String version) {
		LOGGER.info("Ping: name={}, version={}, header={}", buildProperties.getName(), buildProperties.getVersion(), version);
		HttpHeaders headers = new HttpHeaders();
		if (version != null)
			headers.set("x-version", version);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange("http://callme-service:8091/callme/ping", HttpMethod.GET, entity, String.class);
		return buildProperties.getName() + ":" + buildProperties.getVersion() + ". Calling... " + response.getBody() + " with header " + version;
	}

}
