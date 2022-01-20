package com.example.trading;

import com.example.trading.game.GameService;
import com.example.trading.player.PlayerRepository;
import com.example.trading.station.StationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
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

    @Test
    @Transactional
    public void createGameTest() {
        this.gameService.createNewGame(UUID.randomUUID());

        assertNotEquals(
                "[]",
                this.gameService.getGames().toString()
        );
    }
}
