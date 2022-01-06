package com.example.trading.resource;

import com.example.trading.event.DomainEvent;
import com.example.trading.core.kafka.KafkaMessageProducer;
import com.example.trading.event.DomainEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class ResourceEventProducer {
    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;

    @Autowired
    private DomainEventService domainEventService;

    public void publishNewResourcePrices(String resources) {
        DomainEvent event = this.domainEventService.createDomainEvent(
                resources,
                UUID.randomUUID().toString(),
                "1",
                "current-resource-prices"
        );

        this.kafkaMessageProducer.send("current-resource-prices", event);
    }
}
