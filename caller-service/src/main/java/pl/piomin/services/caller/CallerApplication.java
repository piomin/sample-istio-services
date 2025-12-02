package pl.piomin.services.caller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class CallerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CallerApplication.class, args);
	}

	@Bean
	RestClient restClient() {
		return RestClient.builder()
			.baseUrl("http://callme-service:8080")
			.build();
	}

}
