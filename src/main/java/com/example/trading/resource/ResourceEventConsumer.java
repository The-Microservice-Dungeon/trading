package com.example.trading.resource;

import com.example.trading.core.DomainEvent;
import com.example.trading.kafka.KafkaError;
import com.example.trading.kafka.KafkaErrorRepository;
import com.example.trading.station.PlanetDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class ResourceEventConsumer {
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private KafkaErrorRepository kafkaErrorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "resource created", groupId = "trading", autoStartup = "true")
    public void listenToResourceCreation(ConsumerRecord<String, String> consumerRecord) {
        try {
            ResourceDto resource = this.objectMapper.readValue(consumerRecord.value(), ResourceDto.class);
            DomainEvent event = new DomainEvent(resource.toString(), consumerRecord.headers());

            this.resourceService.createResource(resource);
        } catch (Exception e) {
            String errorMsg = "Error while consuming resource event: " + consumerRecord + "\n" + e.getMessage();
            KafkaError err = new KafkaError(consumerRecord.value() + e.getMessage());
            this.kafkaErrorRepository.save(err);
        }
    }
}
