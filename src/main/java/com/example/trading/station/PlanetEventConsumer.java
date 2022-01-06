package com.example.trading.station;

import com.example.trading.core.DomainEvent;
import com.example.trading.kafka.KafkaError;
import com.example.trading.kafka.KafkaErrorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PlanetEventConsumer {
    @Autowired
    private PlanetService planetService;

    @Autowired
    private KafkaErrorRepository kafkaErrorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "spacestation-created", groupId = "trading", autoStartup = "true")
    public void listenToSpaceStationCreation(ConsumerRecord<String, String> consumerRecord) {
        try {
            PlanetDto planet = this.objectMapper.readValue(consumerRecord.value(), PlanetDto.class);
            DomainEvent event = new DomainEvent(planet.toString(), consumerRecord.headers());
            this.planetService.createNewPlanet(planet);
        } catch (Exception e) {
            String errorMsg = "Error while consuming station event: " + consumerRecord + "\n" + e.getMessage();
            KafkaError err = new KafkaError(consumerRecord.value() + e.getMessage());
            this.kafkaErrorRepository.save(err);
        }
    }
}
