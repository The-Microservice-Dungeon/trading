package dungeon.trading.resource;

import dungeon.trading.event.DomainEvent;
import dungeon.trading.core.kafka.KafkaMessageProducer;
import dungeon.trading.event.DomainEventService;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ResourceEventProducer {
    private final KafkaMessageProducer kafkaMessageProducer;

    private final DomainEventService domainEventService;

    public ResourceEventProducer(
        KafkaMessageProducer kafkaMessageProducer, DomainEventService domainEventService) {
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.domainEventService = domainEventService;
    }

    public void publishNewResourcePrices(JSONArray resources) {
        DomainEvent event = this.domainEventService.createDomainEvent(
                resources.toString(),
                UUID.randomUUID().toString(),
                "1",
                "current-resource-prices"
        );

        this.kafkaMessageProducer.send("current-resource-prices", event);
    }
}
