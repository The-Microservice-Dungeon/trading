package dungeon.trading.player;

import dungeon.trading.event.DomainEvent;
import dungeon.trading.core.kafka.KafkaMessageProducer;
import dungeon.trading.event.DomainEventService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PlayerEventProducer {
    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;

    @Autowired
    private DomainEventService domainEventService;

    public void publishPlayerBankCreation(UUID newPlayerId, int moneyAmount, String transactionId) {
        JSONObject payload = new JSONObject();
        payload.put("playerId", newPlayerId.toString());
        payload.put("money", moneyAmount);

        DomainEvent event = this.domainEventService.createDomainEvent(
                payload.toString(),
                transactionId,
                "1",
                "bank-created"
        );

        this.kafkaMessageProducer.send("bank-created", event);
    }
}
