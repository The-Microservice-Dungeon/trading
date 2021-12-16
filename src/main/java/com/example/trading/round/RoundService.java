package com.example.trading.round;

import org.springframework.stereotype.Service;

@Service
public class RoundService {

    public void updateRound(RoundDto roundDto) {
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

