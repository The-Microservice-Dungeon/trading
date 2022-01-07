package com.example.trading;

import com.example.trading.game.RoundDto;
import com.example.trading.game.GameService;
import com.example.trading.item.ItemRepository;
import com.example.trading.item.ItemService;
import com.example.trading.player.Player;
import com.example.trading.player.PlayerRepository;
import com.example.trading.resource.ResourceRepository;
import com.example.trading.station.Station;
import com.example.trading.station.StationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TradingGameServiceTests {
    private final GameService gameService;
    private final PlayerRepository playerRepository;
    private final StationRepository stationRepository;

    @Autowired
    public TradingGameServiceTests(GameService gameService,
                                   PlayerRepository playerRepository,
                                   StationRepository stationRepository) {
        this.gameService = gameService;
        this.playerRepository = playerRepository;
        this.stationRepository = stationRepository;
    }

    @AfterEach
    public void clearRound() {
        RoundDto dto = new RoundDto(0, "init");
        this.gameService.updateRound(dto);
    }

    @Test
    @Transactional
    public void initGameTest() {
        assertEquals(0, this.gameService.getRoundCount());
        assertEquals("init", this.gameService.getRoundStatus());
    }

    @Test
    @Transactional
    public void updateRoundTest() {
        RoundDto dto = new RoundDto(1, "started");
        this.gameService.updateRound(dto);

        assertEquals(1, this.gameService.getRoundCount());
        assertEquals("started", this.gameService.getRoundStatus());
    }

    @Test
    @Transactional
    public void startNewGameTest() {
        this.playerRepository.save(new Player(UUID.randomUUID(), 200));
        this.stationRepository.save(new Station(UUID.randomUUID()));

        this.gameService.startNewGame(UUID.randomUUID().toString());

        assertEquals("[]", this.playerRepository.findAll().toString());
        assertEquals("[]", this.stationRepository.findAll().toString());
    }
}
