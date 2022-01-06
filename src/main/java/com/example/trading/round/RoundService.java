package com.example.trading.round;

import com.example.trading.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RoundService {

    @Autowired
    private PlayerService playerService;

    public RoundService() {
        Round.updateRoundCount(0);
        Round.updateStatus("init");
    }

    public void updateRound(RoundDto roundDto) {
        if (Objects.equals(roundDto.roundStatus, "ended")) this.playerService.updatePlayerBalanceHistories(getRoundCount());
        Round.updateStatus(roundDto.roundStatus);
        Round.updateRoundCount(roundDto.roundNumber);
    }

    public int getRoundCount() {
        return Round.getCurrentRound();
    }

    public String getRoundStatus() {
        return Round.getCurrentStatus();
    }
}

