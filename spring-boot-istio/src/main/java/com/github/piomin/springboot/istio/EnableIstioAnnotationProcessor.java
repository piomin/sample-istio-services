package com.github.piomin.springboot.istio;

import me.snowdrop.istio.api.Duration;
import me.snowdrop.istio.api.networking.v1beta1.*;
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
            DestinationRule
                    dr = istioClient.v1beta1DestinationRule().withName(istioService.getApplicationName() + "-destination").get();
            if (dr == null) {
                dr = new DestinationRuleBuilder()
                        .withNewMetadata().withName(istioService.getApplicationName() + "-destination").endMetadata()
                        .withNewSpec()
                            .withNewHost(istioService.getApplicationName())
                            .withSubsets(istioService.buildSubset(enableIstioAnnotation))
                            .withTrafficPolicy(istioService.buildCircuitBreaker(enableIstioAnnotation))
                        .endSpec()
                        .build();
                istioClient.v1beta1DestinationRule().create(dr);
                LOGGER.info("New DestinationRule created: {}", dr);
            } else {
                LOGGER.info("Found DestinationRule: {}", dr);
                if (!enableIstioAnnotation.version().isEmpty()) {
                    Optional<Subset> subset = dr.getSpec().getSubsets().stream()
                            .filter(s -> s.getName().equals(enableIstioAnnotation.version()))
                            .findAny();
                    if (subset.isEmpty()) {
                        istioClient.v1beta1DestinationRule().withName(istioService.getApplicationName() + "-destination")
                                .edit()
                                .editSpec()
                                .addNewSubsetLike(istioService.buildSubset(enableIstioAnnotation)).endSubset()
                                .editOrNewTrafficPolicyLike(istioService.buildCircuitBreaker(enableIstioAnnotation)).endTrafficPolicy()
                                .endSpec()
                                .done();
                    }
                }
            }
            VirtualService vs = istioClient.v1beta1VirtualService().withName(istioService.getApplicationName() + "-route").get();
            if (vs == null) {
                 vs = new VirtualServiceBuilder()
                        .withNewMetadata().withName(istioService.getApplicationName() + "-route").endMetadata()
                        .withNewSpec()
                            .addToHosts(istioService.getApplicationName())
                            .addNewHttp()
                                .withTimeout(enableIstioAnnotation.timeout() == 0 ? null : new Duration(0, (long) enableIstioAnnotation.timeout()))
                                .withNewRetriesLike(istioService.buildRetry(enableIstioAnnotation)).endRetries()
                                .addNewRoute().withNewDestinationLike(istioService.buildRoute(enableIstioAnnotation)).endDestination().endRoute()
                            .endHttp()
                        .endSpec()
                        .build();
                istioClient.v1beta1VirtualService().create(vs);
                LOGGER.info("New VirtualService created: {}", vs);
            } else {
                LOGGER.info("Found VirtualService: {}", vs);
                if (!enableIstioAnnotation.version().isEmpty()) {
                    istioClient.v1beta1VirtualService().withName(istioService.getApplicationName() + "-route")
                            .edit()
                            .editSpec()
                            .editFirstHttp()
                                .withTimeout(enableIstioAnnotation.timeout() == 0 ? null : new Duration(0, (long) enableIstioAnnotation.timeout()))
                                .withRetries(istioService.buildRetry(enableIstioAnnotation))
                            .editFirstRoute()
                                .withWeight(enableIstioAnnotation.weight() == 0 ? null: enableIstioAnnotation.weight())
                                .editOrNewDestinationLike(istioService.buildRoute(enableIstioAnnotation)).endDestination()
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
