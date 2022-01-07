package com.example.trading.game;

import com.example.trading.event.DomainEvent;
import com.example.trading.core.kafka.error.KafkaError;
import com.example.trading.core.kafka.error.KafkaErrorRepository;
import com.example.trading.event.DomainEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Objects;

public class GameEventConsumer {
    @Autowired
    private GameService gameService;

    @Autowired
    private DomainEventService domainEventService;

    @Autowired
    private KafkaErrorRepository kafkaErrorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "status", groupId = "trading", autoStartup = "true")
    public void listenToGameStatus(ConsumerRecord<String, String> consumerRecord) {
        try {
            GameStatusDto statusDto = this.objectMapper.readValue(consumerRecord.value(), GameStatusDto.class);
            this.domainEventService.saveDomainEvent(statusDto.toString(), consumerRecord.headers());

            if (Objects.equals(statusDto.status, "created")) {
                this.gameService.startNewGame(statusDto.gameId);
            }
        } catch (Exception e) {
            String errorMsg = "Error while consuming status event: " + consumerRecord + "\n" + e.getMessage();
            KafkaError err = new KafkaError(consumerRecord.value() + e.getMessage());
            this.kafkaErrorRepository.save(err);
        }
    }

    @KafkaListener(topics = "roundStatus", groupId = "trading", autoStartup = "true")
    public void listenToRoundStarted(ConsumerRecord<String, String> consumerRecord) {
        try {
            RoundDto round = this.objectMapper.readValue(consumerRecord.value(), RoundDto.class);
            this.domainEventService.saveDomainEvent(round.toString(), consumerRecord.headers());
            this.gameService.updateRound(round);

        } catch (Exception e) {
            String errorMsg = "Error while consuming round status event: " + consumerRecord + "\n" + e.getMessage();
            KafkaError err = new KafkaError(consumerRecord.value() + e.getMessage());
            this.kafkaErrorRepository.save(err);
        }
    }
}
