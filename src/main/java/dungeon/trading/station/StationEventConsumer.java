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
    private final StationService stationService;

    private final DomainEventService domainEventService;

    private final KafkaErrorService kafkaErrorService;

    private final ObjectMapper objectMapper;

    public StationEventConsumer(ObjectMapper objectMapper, StationService stationService,
        DomainEventService domainEventService, KafkaErrorService kafkaErrorService) {
        this.objectMapper = objectMapper;
        this.stationService = stationService;
        this.domainEventService = domainEventService;
        this.kafkaErrorService = kafkaErrorService;
    }

    @KafkaListener(topics = "spacestation-created", groupId = "trading", autoStartup = "true")
    public void listenToSpaceStationCreation(ConsumerRecord<String, String> consumerRecord) {
        try {
            StationDto stationDto = this.objectMapper.readValue(consumerRecord.value(), StationDto.class);
            this.domainEventService.saveDomainEvent(
                    "{\"station_id\":" + stationDto.planet_id + "}",
                    consumerRecord.headers()
            );
            this.stationService.createNewStation(stationDto);
        } catch (Exception e) {
            this.kafkaErrorService.newKafkaError("spacestation-created", consumerRecord.toString(), e.getMessage());
        }
    }
}
