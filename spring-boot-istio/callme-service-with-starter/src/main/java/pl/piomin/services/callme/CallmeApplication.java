package pl.piomin.services.callme;

import com.github.piomin.springboot.istio.annotation.EnableIstio;
import com.github.piomin.springboot.istio.annotation.Fault;
import com.github.piomin.springboot.istio.annotation.Match;
import com.github.piomin.springboot.istio.annotation.MatchType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableIstio(enableGateway = true, version = "v1",
	matches = {@Match(type = MatchType.HEADERS, key = "X-Version", value = "v1")})
public class CallmeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CallmeApplication.class, args);
	}
	
}
