package com.example.trading.game;

import com.example.trading.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GameService {

    @Autowired
    private PlayerService playerService;

    public GameService() {
        Game.updateRoundCount(0);
        Game.updateStatus("init");
    }

    public void startNewGame() {
        Game.updateRoundCount(0);
        Game.updateStatus("init");
    }

    public void updateRound(RoundDto roundDto) {
        if (Objects.equals(roundDto.roundStatus, "ended")) this.playerService.updatePlayerBalanceHistories(getRoundCount());
        Game.updateStatus(roundDto.roundStatus);
        Game.updateRoundCount(roundDto.roundNumber);
    }

    public int getRoundCount() {
        return Game.getCurrentRound();
    }

    public String getRoundStatus() {
        return Game.getCurrentStatus();
    }
}

