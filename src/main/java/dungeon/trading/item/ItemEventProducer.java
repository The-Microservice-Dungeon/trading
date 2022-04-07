package dungeon.trading.item;

import dungeon.trading.event.DomainEvent;
import dungeon.trading.core.kafka.KafkaMessageProducer;
import dungeon.trading.event.DomainEventService;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ItemEventProducer {
    private final KafkaMessageProducer kafkaMessageProducer;

    private final DomainEventService domainEventService;

    public ItemEventProducer(
        KafkaMessageProducer kafkaMessageProducer, DomainEventService domainEventService) {
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.domainEventService = domainEventService;
    }

    public void publishNewItemPrices(JSONArray items) {
        DomainEvent event = this.domainEventService.createDomainEvent(
                items.toString(),
                UUID.randomUUID().toString(),
                "1",
                "current-item-prices"
        );

        this.kafkaMessageProducer.send("current-item-prices", event);
    }
}
