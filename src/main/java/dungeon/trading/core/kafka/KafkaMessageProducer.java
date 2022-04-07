package dungeon.trading.core.kafka;

import dungeon.trading.core.BeanUtil;
import dungeon.trading.core.kafka.error.KafkaErrorService;
import dungeon.trading.event.DomainEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class KafkaMessageProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private BeanUtil beanUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaErrorService kafkaErrorService;

    private final List<Pair<String, DomainEvent>> errors = new ArrayList<>();

    public void send(String topic, DomainEvent event) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, event.eventId, event.payload);
        record.headers().add("eventId", event.eventId.getBytes());
        record.headers().add("transactionId", event.transactionId.getBytes());
        record.headers().add("version", event.version.getBytes());
        record.headers().add("timestamp", event.timestamp.getBytes());
        record.headers().add("type", event.type.getBytes());

        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                String errorMessage = "Couldn't send message: " + record + "\n" + ex.getMessage();
                errors.add(Pair.of(topic, event));
//                kafkaErrorService.newKafkaError(topic, ex.getMessage());
                KafkaErrorService kafkaErrorService = (KafkaErrorService) beanUtil.getBean(KafkaErrorService.class);
                kafkaErrorService.newKafkaError(topic, ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.debug("Successfully sent event");
            }
        });
    }

    @Scheduled(initialDelay = 30000L, fixedDelay = 15000)
    public void retryEvent() {
        for (Pair<String, DomainEvent> errorEvent : this.errors) {
            this.errors.remove(errorEvent);
            send(errorEvent.getFirst(), errorEvent.getSecond());
        }
    }
}
