package com.example.trading.item;

import com.example.trading.event.DomainEvent;
import com.example.trading.core.kafka.KafkaMessageProducer;
import com.example.trading.event.DomainEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class ItemEventProducer {
    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;

    @Autowired
    private DomainEventService domainEventService;

    public void publishNewItemPrices(String items) {
        DomainEvent event = this.domainEventService.createDomainEvent(
                items,
                UUID.randomUUID().toString(),
                "1",
                "current-item-prices"
        );

        this.kafkaMessageProducer.send("current-item-prices", event);
    }
}
