package com.github.piomin.springboot.istio;

import me.snowdrop.istio.api.networking.v1alpha3.*;
import me.snowdrop.istio.client.IstioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

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
            DestinationRule dr = istioClient.destinationRule().withName(istioService.getApplicationName() + "-rule").get();
            if (dr == null) {
                dr = new DestinationRuleBuilder().withApiVersion("networking.istio.io/v1alpha3")
                        .withNewMetadata().withName(istioService.getApplicationName() + "-rule").endMetadata()
                        .withNewSpec()
                        .withNewHost(istioService.getApplicationName())
                        .withSubsets(new SubsetBuilder().withNewName("v1").addToLabels("version", "v1").build())
                        .endSpec()
                        .build();
                istioClient.destinationRule().create(dr);
                LOGGER.info("New DestinationRule created: {}", dr);
            } else {
                LOGGER.info("Found DestinationRule: {}", dr);
            }
            VirtualService vs = istioClient.virtualService().withName(istioService.getApplicationName() + "-route").get();
            if (vs == null) {
                 vs = new VirtualServiceBuilder().withApiVersion("networking.istio.io/v1alpha3")
                        .withNewMetadata().withName(istioService.getApplicationName() + "-route").endMetadata()
                        .withNewSpec()
                        .addToHosts("")
                        .addNewHttp()
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
            }
        }
        return bean;
    }

}
