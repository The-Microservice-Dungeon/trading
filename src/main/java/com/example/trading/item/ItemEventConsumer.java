package com.example.trading.item;

import com.example.trading.core.DomainEvent;
import com.example.trading.kafka.KafkaError;
import com.example.trading.kafka.KafkaErrorRepository;
import com.example.trading.resource.ResourceDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class ItemEventConsumer {
    @Autowired
    private ItemService itemService;

    @Autowired
    private KafkaErrorRepository kafkaErrorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "item created", groupId = "trading", autoStartup = "true")
    public void listenToResourceCreation(ConsumerRecord<String, String> consumerRecord) {
        try {
            ItemDto item = this.objectMapper.readValue(consumerRecord.value(), ItemDto.class);
            DomainEvent event = new DomainEvent(item.toString(), consumerRecord.headers());

            this.itemService.createItem(item);
        } catch (Exception e) {
            String errorMsg = "Error while consuming resource event: " + consumerRecord + "\n" + e.getMessage();
            KafkaError err = new KafkaError(consumerRecord.value() + e.getMessage());
            this.kafkaErrorRepository.save(err);
        }
    }
}
