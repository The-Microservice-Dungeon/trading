package com.example.trading.item;

import com.example.trading.core.DomainEvent;
import com.example.trading.kafka.KafkaMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ItemEventProducer {
    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;

    public void publishNewItemPrices(String items) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        DomainEvent event = new DomainEvent(
                items,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "1",
                sdf.format(new Date()).toString(),
                "current-item-prices"
        );

        this.kafkaMessageProducer.send("current-item-prices", event);
    }
}
