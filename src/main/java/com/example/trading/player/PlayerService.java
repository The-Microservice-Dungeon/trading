package com.example.trading.player;

import com.example.trading.core.exceptions.PlayerDoesNotExistException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerEventProducer playerEventProducer;

    /**
     * creates player (used for testing)
     * @param amount wanted amount for the player bank
     * @return uuid of created player
     */
    public UUID createPlayer(int amount) {
        Player player = new Player(UUID.randomUUID(), amount);
        this.playerRepository.save(player);
        return player.getPlayerId();
    }

    /**
     * creates player and publishes player bank event
     * @param playerStatusDto dto from player-status event
     */
    public void createPlayer(PlayerStatusDto playerStatusDto, String transactionId) {
        Player player = new Player(UUID.fromString(playerStatusDto.userId), 200);
        this.playerRepository.save(player);
        this.playerEventProducer.publishPlayerBankCreation(player.getPlayerId(), player.getMoneyAmount(), transactionId);
    }

    /**
     * checks if the player as enough money
     * @param playerId player to check
     * @param neededAmount amount to check
     * @return boolean if playerId has neededMoney
     */
    public boolean checkPlayerForMoney(UUID playerId, int neededAmount) {
        Optional<Player> player = this.playerRepository.findById(playerId);
        if (player.isEmpty()) throw new PlayerDoesNotExistException(playerId.toString());

        return player.get().getMoneyAmount() >= neededAmount;
    }

    /**
     * reduces player money
     * @param playerId
     * @param amount to reduce
     * @return new money amount
     */
    public int reduceMoney(UUID playerId, int amount) {
        Optional<Player> player = this.playerRepository.findById(playerId);
        if (player.isEmpty()) throw new PlayerDoesNotExistException(playerId.toString());

        return player.get().reduceMoney(amount);
    }

    /**
     * adds player money
     * @param playerId
     * @param amount to add
     * @return new money amount
     */
    public int addMoney(UUID playerId, int amount) {
        Optional<Player> player = this.playerRepository.findById(playerId);
        if (player.isEmpty()) throw new PlayerDoesNotExistException(playerId.toString());

        return player.get().addMoney(amount);
    }

    /**
     * gets current money amount
     * @param playerId
     * @return money amount
     */
    public int getCurrentMoneyAmount(UUID playerId) {
        Optional<Player> player = this.playerRepository.findById(playerId);
        if (player.isEmpty()) throw new PlayerDoesNotExistException(playerId.toString());

        return player.get().getMoneyAmount();
    }

    /**
     * returns all player balances for the rest call
     * @return array with all player balances
     */
    public JSONArray getAllCurrentPlayerBalances() {
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

    public void updatePlayerBalanceHistories(int currentRound) {
        Iterable<Player> players = this.playerRepository.findAll();

        for (Player player : players) {
            player.addCurrentBalanceToHistory(currentRound);
        }
    }

    public JSONArray getPlayerBalancesForRound(int roundNumber) {
        Iterable<Player> players = this.playerRepository.findAll();
        JSONArray balances = new JSONArray();

        for (Player player : players) {
            JSONObject jsonBalance = new JSONObject();
            jsonBalance.put("round", roundNumber);
            jsonBalance.put("player-id", player.getPlayerId().toString());
            jsonBalance.put("balance", player.getMoneyAmountFromRound(roundNumber));
            balances.appendElement(jsonBalance);
        }

        return balances;
    }
}
