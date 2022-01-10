package com.example.trading;

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
public class TradingTestingController {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/stations")
    public ResponseEntity<?> getInformationAboutAllResources() {
        Iterable<Station> stations = this.stationRepository.findAll();
        return new ResponseEntity<Iterable<Station>>(stations, HttpStatus.OK);
    }

    @GetMapping("/players")
    public ResponseEntity<?> getPlayers() {
        Iterable<Player> players = this.playerRepository.findAll();
        return new ResponseEntity<Iterable<Player>>(players, HttpStatus.OK);
    }
}
