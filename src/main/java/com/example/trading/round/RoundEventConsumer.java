package com.example.trading.round;

import com.example.trading.core.DomainEvent;
import com.example.trading.kafka.KafkaError;
import com.example.trading.kafka.KafkaErrorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class RoundEventConsumer {
    @Autowired
    private RoundService roundService;

    @Autowired
    private KafkaErrorRepository kafkaErrorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "round-started", groupId = "trading", autoStartup = "true")
    public void listenToRoundStarted(ConsumerRecord<String, String> consumerRecord) {
        try {
            RoundDto round = this.objectMapper.readValue(consumerRecord.value(), RoundDto.class);
            DomainEvent event = new DomainEvent(round.toString(), consumerRecord.headers());

            this.roundService.updateRound(round);

        } catch (Exception e) {
            String errorMsg = "Error while consuming spawn event: " + consumerRecord + "\n" + e.getMessage();
            KafkaError err = new KafkaError(consumerRecord.value() + e.getMessage());
            this.kafkaErrorRepository.save(err);
        }
    }
}
