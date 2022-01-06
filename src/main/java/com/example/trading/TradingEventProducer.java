package com.example.trading;

import com.example.trading.event.DomainEvent;
import com.example.trading.core.kafka.KafkaMessageProducer;
import com.example.trading.event.DomainEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TradingEventProducer {
    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;

    @Autowired
    private DomainEventService domainEventService;

    public void publishTradingResult(String payload, String transactionId, String eventType) {
        DomainEvent event = this.domainEventService.createDomainEvent(
                payload,
                transactionId,
                "1",
                eventType
        );

        this.kafkaMessageProducer.send("trades", event);
    }
}
