package com.example.trading.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public Integer createPlayer(int amount) {
        Player player = new Player(amount);
        this.playerRepository.save(player);
        return player.getPlayerId();
    }

    public boolean checkPlayerForMoney(int playerId, int neededAmount) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().getMoneyAmount() >= neededAmount;
    }

    public int reduceMoney(int playerId, int amount) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().reduceMoney(amount);
    }

    public int addMoney(int playerId, int amount) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().addMoney(amount);
    }

    public int getCurrentMoneyAmount(int playerId) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().getMoneyAmount();
    }
}
