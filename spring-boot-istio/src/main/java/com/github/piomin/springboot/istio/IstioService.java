package com.github.piomin.springboot.istio;

import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

public class IstioService {

    @Value("${spring.application.name}")
    private Optional<String> applicationName;

    public String getApplicationName() {
        if (applicationName.isPresent())
            return applicationName.get();
        else
            return "default";
    }

}
