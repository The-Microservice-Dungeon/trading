package dungeon.trading.player;

import dungeon.trading.core.kafka.error.KafkaErrorService;
import dungeon.trading.event.DomainEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PlayerEventConsumer {
    private final PlayerService playerService;

    private final DomainEventService domainEventService;

    private final KafkaErrorService kafkaErrorService;

    private final ObjectMapper objectMapper;

    public PlayerEventConsumer(PlayerService playerService,
        DomainEventService domainEventService, KafkaErrorService kafkaErrorService,
        ObjectMapper objectMapper) {
        this.playerService = playerService;
        this.domainEventService = domainEventService;
        this.kafkaErrorService = kafkaErrorService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "playerStatus", groupId = "trading", autoStartup = "true")
    public void listenToPlayerCreation(ConsumerRecord<String, String> consumerRecord) {
        try {
            PlayerStatusDto player = this.objectMapper.readValue(consumerRecord.value(), PlayerStatusDto.class);
            String transactionID = this.domainEventService.saveDomainEvent(
                    "{\"playerId\":" + player.playerId + ",\"name\":\"" + player.name + "\"}",
                    consumerRecord.headers()
            );

            this.playerService.createPlayer(player, transactionID);
        } catch (Exception e) {
            this.kafkaErrorService.newKafkaError("playerStatus", consumerRecord.toString(), e.getMessage());
        }
    }
}
