package com.example.trading.player;

import com.example.trading.event.DomainEvent;
import com.example.trading.core.kafka.error.KafkaError;
import com.example.trading.core.kafka.error.KafkaErrorRepository;
import com.example.trading.event.DomainEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PlayerEventConsumer {
    @Autowired
    private PlayerService playerService;

    @Autowired
    private DomainEventService domainEventService;

    @Autowired
    private KafkaErrorRepository kafkaErrorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "player", groupId = "trading", autoStartup = "true")
    public void listenToPlayerCreation(ConsumerRecord<String, String> consumerRecord) {
        try {
            PlayerStatusDto player = this.objectMapper.readValue(consumerRecord.value(), PlayerStatusDto.class);
            String transactionID = this.domainEventService.saveDomainEvent(player.toString(), consumerRecord.headers());

            if (Objects.equals(player.lobbyAction, "joined")) {
                this.playerService.createPlayer(player, transactionID);
            }

        } catch (Exception e) {
            String errorMsg = "Error while consuming player event: " + consumerRecord + "\n" + e.getMessage();
            KafkaError err = new KafkaError(consumerRecord.value() + e.getMessage());
            this.kafkaErrorRepository.save(err);
        }
    }
}
