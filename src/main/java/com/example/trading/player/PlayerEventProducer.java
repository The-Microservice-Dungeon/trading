package com.example.trading.player;

import com.example.trading.core.DomainEvent;
import com.example.trading.kafka.KafkaErrorRepository;
import com.example.trading.kafka.KafkaMessageProducer;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class PlayerEventProducer {
    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;

    public void publishPlayerBankCreation(UUID newPlayerId, int moneyAmount) {
        JSONObject payload = new JSONObject();
        payload.put("playerId", newPlayerId.toString());
        payload.put("money", moneyAmount);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        DomainEvent event = new DomainEvent(
                payload.toString(),
                UUID.randomUUID().toString(),
                newPlayerId.toString(),
                "1",
                sdf.format(new Date()).toString(),
                "bank-created"
        );

        this.kafkaMessageProducer.send("bank-created", event);
    }
}
