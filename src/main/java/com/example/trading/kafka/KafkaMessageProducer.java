package com.example.trading.kafka;

import com.example.trading.core.BeanUtil;
import com.example.trading.core.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class KafkaMessageProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private BeanUtil beanUtil;

    private ObjectMapper objectMapper;

    private List<Pair<String, DomainEvent>> errors;

    public void send(String topic, DomainEvent event) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, event.eventId, event.payload);
        record.headers().add("eventId", event.eventId.getBytes());
        record.headers().add("transactionId", event.transactionId.getBytes());
        record.headers().add("version", event.version.toString().getBytes());
        record.headers().add("timestamp", event.timestamp.getBytes());
        record.headers().add("type", event.type.getBytes());

        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                String errorMessage = "Couldn't send message: " + record + "\n" + ex.getMessage();
                errors.add(Pair.of(topic, event));
//                beanUtil.getBean(KafkaErrorRepository.class).save(KafkaError(errorMessage));
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
//              ?
            }
        });
    }

    @Scheduled(initialDelay = 30000L, fixedDelay = 15000)
    public void retryEvent() {
        for (Pair<String, DomainEvent> errorEvent : errors) {
            errors.remove(errorEvent);
            send(errorEvent.getFirst(), errorEvent.getSecond());
        }
    }
}
