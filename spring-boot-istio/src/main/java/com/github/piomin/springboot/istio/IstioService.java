package com.github.piomin.springboot.istio;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import me.snowdrop.istio.api.Duration;
import me.snowdrop.istio.api.UInt32ValueBuilder;
import me.snowdrop.istio.api.networking.v1beta1.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

public class IstioService {

    private static final String RETRY_CODES = "5xx";
    private static final long CIRCUIT_OPEN_TIME = 30000L;
    private static final int MAX_DISABLED_HOSTS_PERCENTAGE = 100;

    @Value("${spring.application.name}")
    private Optional<String> applicationName;

    public String getApplicationName() {
        if (applicationName.isPresent())
            return applicationName.get();
        else
            return "default";
    }

    String getDestinationRuleName() {
        return getApplicationName() + "-destination";
    }

    String getVirtualServiceName() {
        return getApplicationName() + "-route";
    }

    ObjectMeta buildDestinationRuleMetadata() {
        return new ObjectMetaBuilder().withName(getDestinationRuleName()).build();
    }

    public TrafficPolicy buildCircuitBreaker(EnableIstio enableIstio) {
        if (enableIstio.circuitBreakerErrors() == 0)
            return null;
        else return new TrafficPolicyBuilder()
                .withConnectionPool(new ConnectionPoolSettingsBuilder().build())
                .withOutlierDetection(new OutlierDetectionBuilder()
                        .withConsecutive5xxErrors(new UInt32ValueBuilder()
                                .withValue(enableIstio.circuitBreakerErrors())
                                .build())
                        .withBaseEjectionTime(new Duration(0, CIRCUIT_OPEN_TIME))
                        .withMaxEjectionPercent(MAX_DISABLED_HOSTS_PERCENTAGE)
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
                .withRetryOn(RETRY_CODES)
                .withPerTryTimeout(new Duration(0, 1000L))
                .build();
    }

    public Destination buildDestination(EnableIstio enableIstio) {
        return new DestinationBuilder()
                .withHost(getApplicationName())
                .withSubset(enableIstio.version())
                .build();
    }

}
