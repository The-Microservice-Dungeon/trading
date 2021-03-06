package dungeon.trading.player;

import dungeon.trading.core.exceptions.PlayerDoesNotExistException;
import java.util.UUID;
import javax.transaction.Transactional;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

  private final PlayerRepository playerRepository;

  private final PlayerEventProducer playerEventProducer;

  public PlayerService(
      PlayerRepository playerRepository, PlayerEventProducer playerEventProducer) {
    this.playerRepository = playerRepository;
    this.playerEventProducer = playerEventProducer;
  }

  /**
   * creates player (used for testing)
   *
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
   *
   * @param playerStatusDto dto from player-status event
   */
  public void createPlayer(PlayerStatusDto playerStatusDto, String transactionId) {
    Player player = new Player(UUID.fromString(playerStatusDto.playerId), 200);
    this.playerRepository.save(player);
    this.playerEventProducer.publishPlayerBankCreation(player.getPlayerId(),
        player.getMoneyAmount(), transactionId);
  }

  /**
   * checks if the player as enough money
   *
   * @param playerId     player to check
   * @param neededAmount amount to check
   * @return boolean if playerId has neededMoney
   */
  public boolean checkPlayerForMoney(UUID playerId, int neededAmount) {
    Player player = this.playerRepository.findById(playerId)
        .orElseThrow(() -> new PlayerDoesNotExistException(playerId.toString()));

    return player.getMoneyAmount() >= neededAmount;
  }

  /**
   * reduces player money
   *
   * @param playerId
   * @param amount   to reduce
   * @return new money amount
   */
  public int reduceMoney(UUID playerId, int amount) {
    Player player = this.playerRepository.findById(playerId)
        .orElseThrow(() -> new PlayerDoesNotExistException(playerId.toString()));

    return player.reduceMoney(amount);
  }

  /**
   * adds player money
   *
   * @param playerId
   * @param amount   to add
   * @return new money amount
   */
  public int addMoney(UUID playerId, int amount) {
    Player player = this.playerRepository.findById(playerId)
        .orElseThrow(() -> new PlayerDoesNotExistException(playerId.toString()));

    return player.addMoney(amount);
  }

  /**
   * gets current money amount
   *
   * @param playerId
   * @return money amount
   */
  public int getCurrentMoneyAmount(UUID playerId) {
    Player player = this.playerRepository.findById(playerId)
        .orElseThrow(() -> new PlayerDoesNotExistException(playerId.toString()));

    return player.getMoneyAmount();
  }

  /**
   * returns all player balances for the rest call
   *
   * @return array with all player balances
   */
  public JSONArray getAllCurrentPlayerBalances() {
    Iterable<Player> players = this.playerRepository.findAll();
    JSONArray balances = new JSONArray();

    for (Player player : players) {
      JSONObject jsonBalance = new JSONObject();
      jsonBalance.put("playerId", player.getPlayerId().toString());
      jsonBalance.put("balance", player.getMoneyAmount());
      balances.appendElement(jsonBalance);
    }

    return balances;
  }

  @Transactional
  public void updatePlayerBalanceHistories(int roundNumber) {
    for (Player player : this.playerRepository.findAll()) {
      player.addCurrentBalanceToHistory(roundNumber);
    }
  }

  public JSONArray getPlayerBalancesForRound(int roundNumber) {
    Iterable<Player> players = this.playerRepository.findAll();
    JSONArray balances = new JSONArray();

    for (Player player : players) {
      JSONObject jsonBalance = new JSONObject();
      jsonBalance.put("round", roundNumber);
      jsonBalance.put("playerId", player.getPlayerId().toString());
      jsonBalance.put("balance", player.getMoneyAmountFromRound(roundNumber));
      balances.appendElement(jsonBalance);
    }

    return balances;
  }

  public void removePlayers() {
    this.playerRepository.deleteAll();
  }
}
