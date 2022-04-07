package dungeon.trading;

import dungeon.trading.core.kafka.error.KafkaError;
import dungeon.trading.core.kafka.error.KafkaErrorRepository;
import dungeon.trading.event.DomainEvent;
import dungeon.trading.event.DomainEventRepository;
import dungeon.trading.game.Game;
import dungeon.trading.game.GameRepository;
import dungeon.trading.game.GameService;
import dungeon.trading.player.Player;
import dungeon.trading.player.PlayerRepository;
import dungeon.trading.station.Station;
import dungeon.trading.station.StationRepository;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TradingDataController {
    private final StationRepository stationRepository;

    private final PlayerRepository playerRepository;

    private final DomainEventRepository domainEventRepository;

    private final KafkaErrorRepository kafkaErrorRepository;

    private final GameService gameService;

    public TradingDataController(
        StationRepository stationRepository, PlayerRepository playerRepository,
        DomainEventRepository domainEventRepository, KafkaErrorRepository kafkaErrorRepository,
        GameService gameService) {
        this.stationRepository = stationRepository;
        this.playerRepository = playerRepository;
        this.domainEventRepository = domainEventRepository;
        this.kafkaErrorRepository = kafkaErrorRepository;
        this.gameService = gameService;
    }

    @GetMapping("/stations")
    public ResponseEntity<?> getStations() {
        Iterable<Station> stations = this.stationRepository.findAll();
        return new ResponseEntity<Iterable<Station>>(stations, HttpStatus.OK);
    }

    @GetMapping("/players")
    public ResponseEntity<?> getPlayers() {
        Iterable<Player> players = this.playerRepository.findAll();
        return new ResponseEntity<Iterable<Player>>(players, HttpStatus.OK);
    }

    @GetMapping("/domainevents")
    public ResponseEntity<?> getDomainEvents() {
        Iterable<DomainEvent> events = this.domainEventRepository.findAll();
        return new ResponseEntity<Iterable<DomainEvent>>(events, HttpStatus.OK);
    }

    @GetMapping("/kafkaerrors")
    public ResponseEntity<?> getKafkaErrors() {
        Iterable<KafkaError> errors = this.kafkaErrorRepository.findAll();
        return new ResponseEntity<Iterable<KafkaError>>(errors, HttpStatus.OK);
    }

    @GetMapping("/games")
    public ResponseEntity<?> getGames() {
        JSONArray games = this.gameService.getAllGames();
        return new ResponseEntity<JSONArray>(games, HttpStatus.OK);
    }
}
