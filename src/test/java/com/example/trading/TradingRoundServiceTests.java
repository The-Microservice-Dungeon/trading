package com.example.trading;

import com.example.trading.game.RoundDto;
import com.example.trading.game.GameService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TradingRoundServiceTests {
    private final GameService gameService;

    @Autowired
    public TradingRoundServiceTests(GameService gameService) {
        this.gameService = gameService;
    }

    @AfterEach
    public void clearRound() {
        RoundDto dto = new RoundDto(0, "init");
        this.gameService.updateRound(dto);
    }

    @Test
    @Transactional
    public void initRoundTest() {
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
}
