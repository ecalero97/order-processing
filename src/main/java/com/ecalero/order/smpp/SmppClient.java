package com.ecalero.order.smpp;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.Address;
import com.ecalero.order.util.AppProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmppClient {

    private final AppProperties appProperties;
    private DefaultSmppClient client;
    private SmppSession session;
    private ScheduledExecutorService enquireLinkExecutor;

    @PostConstruct
    public void init() {
        log.info("Starting SMPP client service with host {} port {} and systemId {}", appProperties.getSmppHost(), appProperties.getSmppPort(), appProperties.getSmppSystemId());
        this.client = new DefaultSmppClient(Executors.newCachedThreadPool(), 1);
        SmppSessionConfiguration config = new SmppSessionConfiguration();
        config.setHost(appProperties.getSmppHost());
        config.setPort(appProperties.getSmppPort());
        config.setSystemId(appProperties.getSmppSystemId());
        config.setPassword(appProperties.getSmppPassword());
        config.setType(com.cloudhopper.smpp.SmppBindType.TRANSCEIVER);

        try {
            this.session = client.bind(config, new DefaultSmppSessionHandler());
            log.info("Session established successfully");
            this.startEnquireLinkTask();
        } catch (InterruptedException ex) {
            log.error("Error on establish SMPP session InterruptedException received", ex);
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            log.error("Error on establish SMPP session ", ex);
        }
    }

    private void startEnquireLinkTask() {
        this.enquireLinkExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread t = new Thread(runnable);
            t.setName("Smpp-EnquireLink-Timer");
            t.setDaemon(true);
            return t;
        });

        this.enquireLinkExecutor.scheduleWithFixedDelay(() -> {
            if (this.session != null && this.session.isBound()) {
                try {
                    this.session.enquireLink(new com.cloudhopper.smpp.pdu.EnquireLink(), 10000);
                } catch (InterruptedException ex) {
                    log.error("Error on send EnquireLink InterruptedException received", ex);
                    Thread.currentThread().interrupt();
                } catch (Exception ex) {
                    log.error("Error on on send EnquireLink", ex);
                }
            }
        }, 15000, 15000, TimeUnit.MILLISECONDS);
    }


    public void sendSms(String phoneNumber, String messageText) {
        if (session == null || !session.isBound()) {
            log.warn("No SMPP session found for send the message");
            return;
        }

        try {
            SubmitSm submit = new SubmitSm();
            submit.setSourceAddress(new Address((byte)0x05, (byte)0x00, "INFO"));
            submit.setDestAddress(new Address((byte)0x01, (byte)0x01, phoneNumber));
            submit.setShortMessage(messageText.getBytes());
            session.submit(submit, 10000);
            log.info("Message send successfully");
        } catch (InterruptedException ex) {
            log.error("Error on send message InterruptedException received", ex);
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            log.error("Error on on send message", ex);
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroying SMPP client service");
        if (enquireLinkExecutor != null) {
            enquireLinkExecutor.shutdownNow();
        }
        if (session != null && session.isBound()) {
            try {
                session.unbind(5000);
            } catch (Exception ex) {
                log.error("Error on unbind session", ex);
            }
            session.destroy();
        }
        if (client != null) {
            client.destroy();
        }
    }
}
