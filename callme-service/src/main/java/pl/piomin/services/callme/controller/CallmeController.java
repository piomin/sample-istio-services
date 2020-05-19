package pl.piomin.services.callme.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/callme")
public class CallmeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CallmeController.class);
	private static final String INSTANCE_ID = UUID.randomUUID().toString();
	private Random random = new Random();

	@Autowired
	BuildProperties buildProperties;
	@Value("${VERSION}")
	private String version;
	
	@GetMapping("/ping")
	public String ping() {
		LOGGER.info("Ping: name={}, version={}", buildProperties.getName(), version);
		return "I'm callme-service " + version;
	}

	@GetMapping("/ping-with-random-error")
	public ResponseEntity<String> pingWithRandomError() {
		int r = random.nextInt(100);
		if (r%2 == 0) {
			LOGGER.info("Ping with random error: name={}, version={}, random={}, httpCode={}",
					buildProperties.getName(), version, r, HttpStatus.GATEWAY_TIMEOUT);
			return new ResponseEntity<>("Surprise " + INSTANCE_ID + " " + version, HttpStatus.GATEWAY_TIMEOUT);
		} else {
			LOGGER.info("Ping with random error: name={}, version={}, random={}, httpCode={}",
					buildProperties.getName(), version, r, HttpStatus.OK);
			return new ResponseEntity<>("I'm callme-service" + INSTANCE_ID + " " + version, HttpStatus.OK);
		}
	}

	@GetMapping("/ping-with-random-delay")
	public String pingWithRandomDelay() throws InterruptedException {
		int r = new Random().nextInt(3000);
		LOGGER.info("Ping with random delay: name={}, version={}, delay={}", buildProperties.getName(), version, r);
		Thread.sleep(r);
		return "I'm callme-service " + version;
	}
	
}
