package com.ecalero.order.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppProperties {

    @Value("${app.apiPort}")
    private int apiPort;

    @Value("${mongo.uri}")
    private String mongoUri;

    @Value("${mongo.database}")
    private String mongoDatabase;

    @Value("${smpp.host}")
    private String smppHost;

    @Value("${smpp.port}")
    private int smppPort;

    @Value("${smpp.systemId}")
    private String smppSystemId;

    @Value("${smpp.password}")
    private String smppPassword;

    @Value("${app.grpcPort}")
    private int grpcPort;
}