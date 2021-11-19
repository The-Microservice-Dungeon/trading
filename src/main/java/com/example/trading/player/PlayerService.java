package com.example.trading.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

//    @KafkaListener(topics = "player created")
    public UUID createPlayer(int amount) {
        Player player = new Player(amount);
        this.playerRepository.save(player);
        return player.getPlayerId();
    }

    public boolean checkPlayerForMoney(UUID playerId, int neededAmount) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().getMoneyAmount() >= neededAmount;
    }

    public int reduceMoney(UUID playerId, int amount) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().reduceMoney(amount);
    }

    public int addMoney(UUID playerId, int amount) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().addMoney(amount);
    }

    public int getCurrentMoneyAmount(UUID playerId) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().getMoneyAmount();
    }
}
