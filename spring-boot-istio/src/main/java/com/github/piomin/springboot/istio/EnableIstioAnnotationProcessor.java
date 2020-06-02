package com.github.piomin.springboot.istio;

import me.snowdrop.istio.api.Duration;
import me.snowdrop.istio.api.networking.v1alpha3.*;
import me.snowdrop.istio.client.IstioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Optional;

public class EnableIstioAnnotationProcessor implements BeanPostProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(EnableIstioAnnotationProcessor.class);
    private ConfigurableListableBeanFactory configurableBeanFactory;
    private IstioClient istioClient;
    private IstioService istioService;

    public EnableIstioAnnotationProcessor(ConfigurableListableBeanFactory configurableBeanFactory, IstioClient istioClient, IstioService istioService) {
        this.configurableBeanFactory = configurableBeanFactory;
        this.istioClient = istioClient;
        this.istioService = istioService;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> managedBeanClass = bean.getClass();
        EnableIstio enableIstioAnnotation =  managedBeanClass.getAnnotation(EnableIstio.class);
        if (enableIstioAnnotation != null) {
            LOGGER.info("Istio feature enabled: {}", enableIstioAnnotation);
            DestinationRule dr = istioClient.destinationRule().withName(istioService.getApplicationName() + "-destination").get();
            if (dr == null) {
                dr = new DestinationRuleBuilder().withApiVersion("networking.istio.io/v1alpha3")
                        .withNewMetadata().withName(istioService.getApplicationName() + "-destination").endMetadata()
                        .withNewSpec()
                            .withNewHost(istioService.getApplicationName())
                            .withSubsets(new SubsetBuilder().withNewName("v1").addToLabels("version", "v1").build())
                            .withTrafficPolicy(enableIstioAnnotation.circuitBreakerErrors() == 0 ? null : new TrafficPolicyBuilder()
                                    .withOutlierDetection(new OutlierDetectionBuilder()
                                            .withConsecutiveErrors(enableIstioAnnotation.circuitBreakerErrors())
                                            .withBaseEjectionTime(new Duration(0, 30000L))
                                            .withMaxEjectionPercent(100)
                                            .build())
                                    .build())
                        .endSpec()
                        .build();
                istioClient.destinationRule().create(dr);
                LOGGER.info("New DestinationRule created: {}", dr);
            } else {
                LOGGER.info("Found DestinationRule: {}", dr);
                if (!enableIstioAnnotation.version().isEmpty()) {
                    Optional<Subset> subset = dr.getSpec().getSubsets().stream()
                            .filter(s -> s.getName().equals(enableIstioAnnotation.version()))
                            .findAny();
                    if (subset.isEmpty()) {
                        istioClient.destinationRule().withName(istioService.getApplicationName() + "-destination")
                                .edit()
                                .editSpec()
                                .addNewSubset()
                                    .withNewName(enableIstioAnnotation.version())
                                    .addToLabels("version", enableIstioAnnotation.version())
                                .endSubset()
                                .editOrNewTrafficPolicy()
                                    .editOrNewOutlierDetection()
                                        .withConsecutiveErrors(enableIstioAnnotation.circuitBreakerErrors())
                                        .withBaseEjectionTime(new Duration(0, 30000L))
                                        .withMaxEjectionPercent(100)
                                    .endOutlierDetection()
                                .endTrafficPolicy()
                                .endSpec()
                                .done();
                    }
                }
            }
            VirtualService vs = istioClient.virtualService().withName(istioService.getApplicationName() + "-route").get();
            if (vs == null) {
                 vs = new VirtualServiceBuilder().withApiVersion("networking.istio.io/v1alpha3")
                        .withNewMetadata().withName(istioService.getApplicationName() + "-route").endMetadata()
                        .withNewSpec()
                            .addToHosts(istioService.getApplicationName())
                            .addNewHttp()
                                .withTimeout(enableIstioAnnotation.timeout() == 0 ? null : new Duration(0, (long) enableIstioAnnotation.timeout()))
                                .withRetries(enableIstioAnnotation.numberOfRetries() == 0 ? null :
                                        new HTTPRetryBuilder()
                                                .withAttempts(enableIstioAnnotation.numberOfRetries())
                                                .withRetryOn("5xx")
                                                .withPerTryTimeout(new Duration(0, 1000L))
                                                .build())
                                .addNewRoute()
                                    .withNewDestination().withHost(istioService.getApplicationName()).withSubset("v1").endDestination()
                                .endRoute()
                            .endHttp()
                        .endSpec()
                        .build();
                istioClient.virtualService().create(vs);
                LOGGER.info("New VirtualService created: {}", vs);
            } else {
                LOGGER.info("Found VirtualService: {}", vs);
                if (!enableIstioAnnotation.version().isEmpty()) {
                    istioClient.virtualService().withName(istioService.getApplicationName() + "-route")
                            .edit()
                            .editSpec()
                            .editFirstHttp()
                                .withTimeout(enableIstioAnnotation.timeout() == 0 ? null : new Duration(0, (long) enableIstioAnnotation.timeout()))
                                .withRetries(enableIstioAnnotation.numberOfRetries() == 0 ? null :
                                        new HTTPRetryBuilder().withAttempts(enableIstioAnnotation.numberOfRetries()).build())
                            .editFirstRoute()
                                .withWeight(enableIstioAnnotation.weight() == 0 ? null: enableIstioAnnotation.weight())
                                .withNewDestination().withHost(istioService.getApplicationName()).withSubset(enableIstioAnnotation.version()).endDestination()
                            .endRoute()
                            .endHttp()
                            .endSpec()
                            .done();
                }
            }
        }
        return bean;
    }

}
