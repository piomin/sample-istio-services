package com.github.piomin.springboot.istio;

import me.snowdrop.istio.client.DefaultIstioClient;
import me.snowdrop.istio.client.IstioClient;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBootIstioAutoConfiguration {

    @Bean
    IstioClient istioClient() {
        return new DefaultIstioClient();
    }

    @Bean
    IstioService istioService() {
        return new IstioService();
    }

    @Bean
    EnableIstioAnnotationProcessor istioAnnotationProcessor(ConfigurableListableBeanFactory configurableBeanFactory) {
        return new EnableIstioAnnotationProcessor(configurableBeanFactory, istioClient(), istioService());
    }

}
