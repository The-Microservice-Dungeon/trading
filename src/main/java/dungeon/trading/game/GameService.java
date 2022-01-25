package dungeon.trading.game;

import dungeon.trading.item.ItemService;
import dungeon.trading.player.PlayerService;
import dungeon.trading.resource.ResourceService;
import dungeon.trading.station.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private StationService stationService;

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

    public void updateRound(RoundDto roundDto) {
        if (Objects.equals(roundDto.roundStatus, "ended")) {
            this.playerService.updatePlayerBalanceHistories(roundDto.roundNumber);
        } else if (Objects.equals(roundDto.roundStatus, "started")) {
            this.itemService.calculateNewItemPrices();
            this.resourceService.calculateNewResourcePrices();
        }

        Optional<Game> newGame = this.gameRepository.findByIsCurrentGame(true);
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
}

