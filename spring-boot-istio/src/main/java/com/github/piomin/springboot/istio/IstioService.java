package com.github.piomin.springboot.istio;

import me.snowdrop.istio.api.Duration;
import me.snowdrop.istio.api.UInt32ValueBuilder;
import me.snowdrop.istio.api.networking.v1beta1.*;
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

    public TrafficPolicy buildCircuitBreaker(EnableIstio enableIstio) {
        if (enableIstio.circuitBreakerErrors() == 0)
            return null;
        else return new TrafficPolicyBuilder()
                .withOutlierDetection(new OutlierDetectionBuilder()
                        .withConsecutive5xxErrors(new UInt32ValueBuilder()
                                .withValue(enableIstio.circuitBreakerErrors())
                                .build())
                        .withBaseEjectionTime(new Duration(0, 30000L))
                        .withMaxEjectionPercent(100)
                        .build())
                .build();
    }

    public Subset buildSubset(EnableIstio enableIstio) {
        return new SubsetBuilder()
                .withName(enableIstio.version())
                .addToLabels("version", enableIstio.version())
                .build();
    }

    public HTTPRetry buildRetry(EnableIstio enableIstio) {
        if (enableIstio.numberOfRetries() == 0)
            return null;
        else return new HTTPRetryBuilder()
                .withAttempts(enableIstio.numberOfRetries())
                .withRetryOn("5xx")
                .withPerTryTimeout(new Duration(0, 1000L))
                .build();
    }

    public Destination buildRoute(EnableIstio enableIstio) {
        return new DestinationBuilder()
                .withHost(getApplicationName())
                .withSubset("v1")
                .build();
    }

}
