package dungeon.trading.game;

import dungeon.trading.item.ItemService;
import dungeon.trading.player.PlayerService;
import dungeon.trading.resource.ResourceService;
import dungeon.trading.station.StationService;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private final GameRepository gameRepository;

  private final PlayerService playerService;

  private final ItemService itemService;

  private final ResourceService resourceService;

  private final StationService stationService;

  public GameService(GameRepository gameRepository,
      PlayerService playerService, ItemService itemService, ResourceService resourceService,
      StationService stationService) {
    this.gameRepository = gameRepository;
    this.playerService = playerService;
    this.itemService = itemService;
    this.resourceService = resourceService;
    this.stationService = stationService;
  }

  @Transactional
  public void createNewGame(UUID newGameId) {
    this.gameRepository.findByIsCurrentGame(true)
        .ifPresent(game -> stopGame(game.getGameId()));

    Game newGame = new Game(newGameId);
    newGame.startGame();
    this.gameRepository.save(newGame);
  }

  @Transactional
  public void stopGame(UUID gameId) {
    Game game = this.gameRepository.findById(gameId).orElseThrow();
    game.stopGame();
    this.itemService.resetItems();
    this.resourceService.resetResources();
    this.playerService.removePlayers();
    this.stationService.removeStations();
    this.gameRepository.save(game);
  }

  @Transactional
  public void updateRound(RoundDto roundDto) {
    if (roundDto.roundNumber == 1) {
      if (this.itemService.getItems().isEmpty()) {
        this.itemService.createAllItems();
      }
      if (this.resourceService.getResources().isEmpty()) {
        this.resourceService.createResources();
      }
    }

    if (Objects.equals(roundDto.roundStatus, "ended")) {
      this.playerService.updatePlayerBalanceHistories(roundDto.roundNumber);
    } else if (Objects.equals(roundDto.roundStatus, "started")) {
      this.itemService.calculateNewItemPrices();
      this.resourceService.calculateNewResourcePrices();
    }

    Optional<Game> newGame = this.gameRepository.findById(UUID.fromString(roundDto.gameId));
    var game = newGame.orElseThrow();
    game.updateRoundCount(roundDto.roundNumber);
    game.updateRoundStatus(roundDto.roundStatus);
  }

  public int getRoundCount() {
    Game newGame = this.gameRepository.findByIsCurrentGame(true).orElseThrow();
    return newGame.getCurrentRound();
  }

  public String getRoundStatus() {
    Game newGame = this.gameRepository.findByIsCurrentGame(true).orElseThrow();
    return newGame.getRoundStatus();
  }

  public UUID getCurrentGameId() {
    Game newGame = this.gameRepository.findByIsCurrentGame(true).orElseThrow();
    return newGame.getGameId();
  }

  public Iterable<Game> getGames() {
    return this.gameRepository.findAll();
  }

  public JSONArray getAllGames() {
    JSONArray array = new JSONArray();

    for (Game game : this.gameRepository.findAll()) {
      JSONObject object = new JSONObject();
      object.put("gameId", game.getGameId());
      object.put("isCurrentGame", game.getIsCurrentGame());
      object.put("currentRound", game.getCurrentRound());
      object.put("roundStatus", game.getRoundStatus());
      array.add(object);
    }

    return array;
  }
}

