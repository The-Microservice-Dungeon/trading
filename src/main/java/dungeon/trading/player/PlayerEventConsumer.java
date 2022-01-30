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
    @Autowired
    private PlayerService playerService;

    @Autowired
    private DomainEventService domainEventService;

    @Autowired
    private KafkaErrorService kafkaErrorService;

    @Autowired
    private ObjectMapper objectMapper;

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
