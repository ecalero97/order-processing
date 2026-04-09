package com.ecalero.order.config;

import com.ecalero.order.util.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebFluxConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    private final AppProperties appProperties;

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        int apiPort = appProperties.getApiPort();
        log.info("Staring server with port {}", apiPort);
        factory.setPort(apiPort);
    }
}
