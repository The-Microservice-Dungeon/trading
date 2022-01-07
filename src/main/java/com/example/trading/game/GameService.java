package com.example.trading.game;

import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.ResourceService;
import com.example.trading.station.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GameService {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private StationService stationService;

    public GameService() {
        Game.updateRoundCount(0);
        Game.updateStatus("init");
    }

    public void startNewGame(String newGameId) {
        this.itemService.resetItems();
        this.resourceService.resetResources();
        this.playerService.removePlayers();
        this.stationService.removeStations();

        Game.updateGameId(newGameId);
        Game.updateRoundCount(0);
        Game.updateStatus("init");
    }

    public void updateRound(RoundDto roundDto) {
        if (Objects.equals(roundDto.roundStatus, "ended")) this.playerService.updatePlayerBalanceHistories(getRoundCount());
        Game.updateStatus(roundDto.roundStatus);
        Game.updateRoundCount(roundDto.roundNumber);
    }

    public int getRoundCount() {
        return Game.getCurrentRound();
    }

    public String getRoundStatus() {
        return Game.getCurrentStatus();
    }

    public String getGameId() {
        return Game.getCurrentGameId();
    }
}

