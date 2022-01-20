package com.example.trading.game;

import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.ResourceService;
import com.example.trading.station.StationService;
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
        Game newGame = new Game(newGameId);
        this.gameRepository.save(newGame);
    }

    public void startNewGame(UUID newGameId) {
        Optional<Game> currentGame = this.gameRepository.findByIsCurrentGame(true);
        if (currentGame.isPresent()) {
            System.out.println("Stop old game: " + currentGame.get().getGameId());
            currentGame.get().stopGame();
            this.itemService.resetItems();
            this.resourceService.resetResources();
            this.playerService.removePlayers();
            this.stationService.removeStations();
        }

        Optional<Game> newGame = this.gameRepository.findById(newGameId);
        newGame.get().startGame();
        this.gameRepository.save(newGame.get());
    }

    public void updateRound(RoundDto roundDto) {
        if (Objects.equals(roundDto.roundStatus, "ended")) {
            this.playerService.updatePlayerBalanceHistories(getRoundCount());
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

