package com.ecalero.order.config;

import akka.actor.ActorSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class AkkaConfig {
    private ActorSystem actorSystem;

    @Bean
    public ActorSystem actorSystem() {
        actorSystem = ActorSystem.create("order-processing-system");
        return actorSystem;
    }

    @EventListener(ContextClosedEvent.class)
    public void onContextClosed() {
        if (actorSystem != null) {
            actorSystem.terminate();
        }
    }
}
