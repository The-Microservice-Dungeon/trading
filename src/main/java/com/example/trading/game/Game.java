package com.example.trading.game;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

/**
 * Game manages the round count and status for our service
 * needed for the calculation of item and resource prices
 * needed for the saving of item and resource histories
 */
@Entity
@Getter
@NoArgsConstructor
public class Game {

    @Id
    private UUID gameId;

    private Boolean isCurrentGame;

    private int currentRound;
    private String roundStatus;

    public Game(UUID newGame) {
        this.gameId = newGame;
        this.currentRound = 0;
        this.roundStatus = "init";
        this.isCurrentGame = false;
    }

    public void updateRoundCount(int newCount) {
        this.currentRound = newCount;
    }

    public void updateRoundStatus(String newStatus) {
        this.roundStatus = newStatus;
    }

    public void stopGame() {
        this.isCurrentGame = false;
    }

    public void startGame() {
        this.isCurrentGame = true;
    }
}
