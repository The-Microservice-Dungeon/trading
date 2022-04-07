package dungeon.trading.game;

import dungeon.trading.core.kafka.error.KafkaErrorService;
import dungeon.trading.event.DomainEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class GameEventConsumer {
    private final GameService gameService;

    private final DomainEventService domainEventService;

    private final KafkaErrorService kafkaErrorService;

    private final ObjectMapper objectMapper;

    public GameEventConsumer(GameService gameService,
        DomainEventService domainEventService, KafkaErrorService kafkaErrorService,
        ObjectMapper objectMapper) {
        this.gameService = gameService;
        this.domainEventService = domainEventService;
        this.kafkaErrorService = kafkaErrorService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "status", groupId = "trading", autoStartup = "true")
    public void listenToGameStatus(ConsumerRecord<String, String> consumerRecord) {
        try {
            GameStatusDto statusDto = this.objectMapper.readValue(consumerRecord.value(), GameStatusDto.class);
            this.domainEventService.saveDomainEvent(
                    "{\"gameId\":" + statusDto.gameId + ",\"status\":\"" + statusDto.status + "\"}",
                    consumerRecord.headers()
            );

            if (Objects.equals(statusDto.status, "created")) {
                this.gameService.createNewGame(UUID.fromString(statusDto.gameId));
            } else if (Objects.equals(statusDto.status, "ended")) {
                this.gameService.stopGame(UUID.fromString(statusDto.gameId));
            }
        } catch (Exception e) {
            log.error("Error during 'status' event consumption", e);
            this.kafkaErrorService.newKafkaError("(game-) status", consumerRecord.toString(), e.getMessage());
        }
    }

    @KafkaListener(topics = "roundStatus", groupId = "trading", autoStartup = "true")
    public void listenToRoundStarted(ConsumerRecord<String, String> consumerRecord) {
        try {
            RoundDto round = this.objectMapper.readValue(consumerRecord.value(), RoundDto.class);

            String payload = "{\"gameId\":\"" + round.gameId +
                    "\",\"roundId\":\"" + round.roundId +
                    "\",\"roundNumber\":" + round.roundNumber +
                    ",\"roundStatus\":\"" + round.roundStatus + "\"}";

            this.domainEventService.saveDomainEvent(payload, consumerRecord.headers());
            this.gameService.updateRound(round);
        } catch (Exception e) {
            log.error("Error during 'roundStatus' event consumption", e);
            this.kafkaErrorService.newKafkaError("roundStatus", consumerRecord.toString(), e.getMessage());
        }
    }
}
