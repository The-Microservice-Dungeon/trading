package com.example.trading;

import com.example.trading.core.DomainEvent;
import com.example.trading.kafka.KafkaMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class TradingEventProducer {
    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;

    public void publishTradingResult(String payload, String transactionId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        DomainEvent event = new DomainEvent(
                payload,
                UUID.randomUUID().toString(),
                transactionId,
                "1",
                sdf.format(new Date()).toString(),
                "trades"
        );

        this.kafkaMessageProducer.send("trades", event);
    }
}
