package pl.piomin.services.caller.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/caller")
public class CallerController {

	@Autowired
	BuildProperties buildProperties;
	
	@GetMapping("/ping")
	public String ping() {
		return "I'm " + buildProperties.getName() + " " + buildProperties.getVersion(); 
	}
	
}
