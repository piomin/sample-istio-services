package com.github.piomin.springboot.istio;

import io.fabric8.kubernetes.client.dsl.Resource;
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
        EnableIstio enableIstioAnnotation =  bean.getClass().getAnnotation(EnableIstio.class);
        if (enableIstioAnnotation != null) {
            LOGGER.info("Istio feature enabled: {}", enableIstioAnnotation);

            Resource<DestinationRule, DoneableDestinationRule> resource = istioClient.v1beta1DestinationRule()
                    .withName(istioService.getDestinationRuleName());
            if (resource.get() == null) {
                createNewDestinationRule(enableIstioAnnotation);
            } else {
                editDestinationRule(enableIstioAnnotation, resource);
            }

            Resource<VirtualService, DoneableVirtualService> resource2 = istioClient.v1beta1VirtualService()
                    .withName(istioService.getVirtualServiceName());
            if (resource2.get() == null) {
                 createNewVirtualService(enableIstioAnnotation);
            } else {
                editVirtualService(enableIstioAnnotation, resource2);
            }
        }
        return bean;
    }

    private void createNewDestinationRule(EnableIstio enableIstioAnnotation) {
        DestinationRule dr = new DestinationRuleBuilder()
                .withMetadata(istioService.buildDestinationRuleMetadata())
                .withNewSpec()
                .withNewHost(istioService.getApplicationName())
                .withSubsets(istioService.buildSubset(enableIstioAnnotation))
                .withTrafficPolicy(istioService.buildCircuitBreaker(enableIstioAnnotation))
                .endSpec()
                .build();
        istioClient.v1beta1DestinationRule().create(dr);
        LOGGER.info("New DestinationRule created: {}", dr);
    }

    private void editDestinationRule(EnableIstio enableIstioAnnotation, Resource<DestinationRule, DoneableDestinationRule> resource) {
        LOGGER.info("Found DestinationRule: {}", resource.get());
        if (!enableIstioAnnotation.version().isEmpty()) {
            Optional<Subset> subset = resource.get().getSpec().getSubsets().stream()
                    .filter(s -> s.getName().equals(enableIstioAnnotation.version()))
                    .findAny();
            resource.edit()
                    .editSpec()
                    .addNewSubsetLike(subset.isEmpty() ? istioService.buildSubset(enableIstioAnnotation) : null).endSubset()
                    .editOrNewTrafficPolicyLike(istioService.buildCircuitBreaker(enableIstioAnnotation)).endTrafficPolicy()
                    .endSpec()
                    .done();
        }
    }

    private void createNewVirtualService(EnableIstio enableIstioAnnotation) {
        VirtualService vs = new VirtualServiceBuilder()
                .withNewMetadata().withName(istioService.getVirtualServiceName()).endMetadata()
                .withNewSpec()
                .addToHosts(istioService.getApplicationName())
                .addNewHttp()
                .withTimeout(enableIstioAnnotation.timeout() == 0 ? null : new Duration(0, (long) enableIstioAnnotation.timeout()))
                .withNewRetriesLike(istioService.buildRetry(enableIstioAnnotation)).endRetries()
                .addNewRoute().withNewDestinationLike(istioService.buildDestination(enableIstioAnnotation)).endDestination().endRoute()
                .endHttp()
                .endSpec()
                .build();
        istioClient.v1beta1VirtualService().create(vs);
        LOGGER.info("New VirtualService created: {}", vs);
    }

    private void editVirtualService(EnableIstio enableIstioAnnotation, Resource<VirtualService, DoneableVirtualService> resource) {
        LOGGER.info("Found VirtualService: {}", resource.get());
        if (!enableIstioAnnotation.version().isEmpty()) {
            istioClient.v1beta1VirtualService().withName(istioService.getVirtualServiceName())
                    .edit()
                    .editSpec()
                    .editFirstHttp()
                    .withTimeout(enableIstioAnnotation.timeout() == 0 ? null : new Duration(0, (long) enableIstioAnnotation.timeout()))
                    .withRetries(istioService.buildRetry(enableIstioAnnotation))
                    .editFirstRoute()
                    .withWeight(enableIstioAnnotation.weight() == 0 ? null: enableIstioAnnotation.weight())
                    .editOrNewDestinationLike(istioService.buildDestination(enableIstioAnnotation)).endDestination()
                    .endRoute()
                    .endHttp()
                    .endSpec()
                    .done();
        }
    }
}
