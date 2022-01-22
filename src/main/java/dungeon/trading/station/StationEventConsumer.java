package dungeon.trading.station;

import dungeon.trading.core.kafka.error.KafkaErrorService;
import dungeon.trading.event.DomainEventService;
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
    private KafkaErrorService kafkaErrorService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "spacestation-created", groupId = "trading", autoStartup = "true")
    public void listenToSpaceStationCreation(ConsumerRecord<String, String> consumerRecord) {
        try {
            StationDto stationDto = this.objectMapper.readValue(consumerRecord.value(), StationDto.class);
            this.domainEventService.saveDomainEvent(stationDto.toString(), consumerRecord.headers());
            this.stationService.createNewStation(stationDto);
        } catch (Exception e) {
            this.kafkaErrorService.newKafkaError("spacestation-created", consumerRecord.toString(), e.getMessage());
        }
    }
}
