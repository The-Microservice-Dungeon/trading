package dungeon.trading.game;

import dungeon.trading.item.ItemService;
import dungeon.trading.player.PlayerService;
import dungeon.trading.resource.ResourceService;
import dungeon.trading.station.StationService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
        Optional<Game> currentGame = this.gameRepository.findByIsCurrentGame(true);
        if (currentGame.isPresent()) {
            currentGame.get().stopGame();
            this.itemService.resetItems();
            this.resourceService.resetResources();
            this.playerService.removePlayers();
            this.stationService.removeStations();
        }

        Game newGame = new Game(newGameId);
        newGame.startGame();
        this.gameRepository.save(newGame);
    }

    @Transactional
    public void stopGame(UUID gameId) {
        Optional<Game> game = this.gameRepository.findById(gameId);
        game.ifPresent(Game::stopGame);
    }

    @Transactional
    public void updateRound(RoundDto roundDto) {
        if (roundDto.roundNumber == 1) {
            if (this.itemService.getItems().isEmpty()) this.itemService.createAllItems();
            if (this.resourceService.getResources().isEmpty()) this.resourceService.createResources();
        }

        if (Objects.equals(roundDto.roundStatus, "ended")) {
            this.playerService.updatePlayerBalanceHistories(roundDto.roundNumber);
        } else if (Objects.equals(roundDto.roundStatus, "started")) {
            this.itemService.calculateNewItemPrices();
            this.resourceService.calculateNewResourcePrices();
        }

        Optional<Game> newGame = this.gameRepository.findById(UUID.fromString(roundDto.gameId));
        newGame.get().updateRoundCount(roundDto.roundNumber);
        newGame.get().updateRoundStatus(roundDto.roundStatus);
    }

    public int getRoundCount() {
        Optional<Game> newGame = this.gameRepository.findByIsCurrentGame(true);
        return newGame.get().getCurrentRound();
    }

    public String getRoundStatus() {
        Optional<Game> newGame = this.gameRepository.findByIsCurrentGame(true);
        return newGame.get().getRoundStatus();
    }

    public UUID getCurrentGameId() {
        Optional<Game> newGame = this.gameRepository.findByIsCurrentGame(true);
        return newGame.get().getGameId();
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

