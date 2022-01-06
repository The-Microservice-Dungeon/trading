package com.example.trading;

import com.example.trading.round.RoundDto;
import com.example.trading.round.RoundService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TradingRoundServiceTests {
    private final RoundService roundService;

    @Autowired
    public TradingRoundServiceTests(RoundService roundService) {
        this.roundService = roundService;
    }

    @AfterEach
    public void clearRound() {
        RoundDto dto = new RoundDto(0, "init");
        this.roundService.updateRound(dto);
    }

    @Test
    @Transactional
    public void initRoundTest() {
        assertEquals(0, this.roundService.getRoundCount());
        assertEquals("init", this.roundService.getRoundStatus());
    }

    @Test
    @Transactional
    public void updateRoundTest() {
        RoundDto dto = new RoundDto(1, "started");
        this.roundService.updateRound(dto);

        assertEquals(1, this.roundService.getRoundCount());
        assertEquals("started", this.roundService.getRoundStatus());
    }
}
