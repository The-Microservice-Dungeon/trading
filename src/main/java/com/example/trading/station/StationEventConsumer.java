package com.example.trading.station;

import com.example.trading.core.kafka.error.KafkaError;
import com.example.trading.core.kafka.error.KafkaErrorRepository;
import com.example.trading.event.DomainEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StationEventConsumer {
    @Autowired
    private StationService stationService;

    @Autowired
    private DomainEventService domainEventService;

    @Autowired
    private KafkaErrorRepository kafkaErrorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "spacestation-created", groupId = "trading", autoStartup = "true")
    public void listenToSpaceStationCreation(ConsumerRecord<String, String> consumerRecord) {
        try {
            StationDto stationDto = this.objectMapper.readValue(consumerRecord.value(), StationDto.class);
            this.domainEventService.saveDomainEvent(stationDto.toString(), consumerRecord.headers());
            this.stationService.createNewStation(stationDto);
        } catch (Exception e) {
            String errorMsg = "Error while consuming station event: " + consumerRecord + "\n" + e.getMessage();
            KafkaError err = new KafkaError(consumerRecord.value() + e.getMessage());
            this.kafkaErrorRepository.save(err);
        }
    }
}