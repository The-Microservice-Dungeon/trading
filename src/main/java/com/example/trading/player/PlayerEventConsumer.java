package com.example.trading.player;

import com.example.trading.core.DomainEvent;
import com.example.trading.kafka.KafkaError;
import com.example.trading.kafka.KafkaErrorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.UUID;

public class PlayerEventConsumer {
    @Autowired
    private PlayerService playerService;

    @Autowired
    private KafkaErrorRepository kafkaErrorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "player joined", groupId = "trading", autoStartup = "true")
    public void listenToPlayerCreation(ConsumerRecord<String, String> consumerRecord) {
        try {
            PlayerDto player = this.objectMapper.readValue(consumerRecord.value(), PlayerDto.class);
            DomainEvent event = new DomainEvent(player.toString(), consumerRecord.headers());

            this.playerService.createPlayer(player);

        } catch (Exception e) {
            String errorMsg = "Error while consuming player event: " + consumerRecord + "\n" + e.getMessage();
            KafkaError err = new KafkaError(consumerRecord.value() + e.getMessage());
            this.kafkaErrorRepository.save(err);
        }
    }

//    maybe ein listener auf player left
}
