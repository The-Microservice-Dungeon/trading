package com.example.trading;

import com.example.trading.core.kafka.error.KafkaError;
import com.example.trading.core.kafka.error.KafkaErrorRepository;
import com.example.trading.event.DomainEvent;
import com.example.trading.event.DomainEventRepository;
import com.example.trading.player.Player;
import com.example.trading.player.PlayerRepository;
import com.example.trading.station.Station;
import com.example.trading.station.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TradingDataController {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private DomainEventRepository domainEventRepository;

    @Autowired
    private KafkaErrorRepository kafkaErrorRepository;

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
}
