package com.example.trading.player;

import com.example.trading.core.DomainEvent;
import com.example.trading.kafka.KafkaMessageProducer;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.internals.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    private PlayerEventProducer playerEventProducer;

    public UUID createPlayer(int amount) {
        Player player = new Player(amount);
        this.playerRepository.save(player);
//        this.playerEventProducer.publishPlayerBankCreation(player.getPlayerId(), player.getMoneyAmount());
        return player.getPlayerId();
    }

    public void createPlayer(PlayerDto playerDto) {
        Player player = new Player(playerDto.playerId, 200);
        this.playerRepository.save(player);
//        this.playerEventProducer.publishPlayerBankCreation(player.getPlayerId(), player.getMoneyAmount());
    }

    public boolean checkPlayerForMoney(UUID playerId, int neededAmount) {
        Optional<Player> player = this.playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().getMoneyAmount() >= neededAmount;
    }

    public int reduceMoney(UUID playerId, int amount) {
        Optional<Player> player = this.playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().reduceMoney(amount);
    }

    public int addMoney(UUID playerId, int amount) {
        Optional<Player> player = this.playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().addMoney(amount);
    }

    public int getCurrentMoneyAmount(UUID playerId) {
        Optional<Player> player = this.playerRepository.findById(playerId);
        if (player.isEmpty()) throw new IllegalArgumentException("The given player does not exist.");

        return player.get().getMoneyAmount();
    }

    public JSONArray getAllPlayerBalances() {
        Iterable<Player> players = this.playerRepository.findAll();

        JSONArray balances = new JSONArray();

        for (Player player : players) {
            JSONObject jsonBalance = new JSONObject();
            jsonBalance.put("player-id", player.getPlayerId().toString());
            jsonBalance.put("balance", player.getMoneyAmount());
            balances.appendElement(jsonBalance);
        }

        return balances;
    }
}
