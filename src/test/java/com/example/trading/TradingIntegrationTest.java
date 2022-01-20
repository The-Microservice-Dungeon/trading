package com.example.trading;

import com.example.trading.game.GameService;
import com.example.trading.game.RoundDto;
import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.ResourceService;
import com.example.trading.station.StationService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TradingIntegrationTest {

    private final PlayerService playerService;
    private final StationService stationService;
    private final ResourceService resourceService;
    private final ItemService itemService;
    private final GameService gameService;

    @Autowired
    public TradingIntegrationTest(PlayerService playerService,
                                  StationService stationService,
                                  ResourceService resourceService,
                                  ItemService itemService,
                                  GameService gameService) {
        this.playerService = playerService;
        this.stationService = stationService;
        this.resourceService = resourceService;
        this.itemService = itemService;
        this.gameService = gameService;
    }

    @Test
    @Transactional
    public void sellResourceSuccessfullyTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.stationService.createNewStation(UUID.randomUUID());

        int gotMoney = (int) this.resourceService.sellResources(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId).get("moneyChangedBy");
        assertTrue(gotMoney >= 0);
    }

    @Test
    @Transactional
    public void buySingleRobotTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetOne = this.stationService.createNewStation(UUID.randomUUID());
        UUID planetTwo = this.stationService.createNewStation(UUID.randomUUID());

        Map<String, ?> moneyChangedBy = this.itemService.buyRobots(UUID.randomUUID(), playerId, 1);
        assertEquals(-100, moneyChangedBy.get("moneyChangedBy"));
    }

    @Test
    @Transactional
    public void buyMultipleRobotsTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetOne = this.stationService.createNewStation(UUID.randomUUID());
        UUID planetTwo = this.stationService.createNewStation(UUID.randomUUID());

        Map<String, ?> moneyChangedBy = this.itemService.buyRobots(UUID.randomUUID(), playerId, 2);
        assertEquals(-200, moneyChangedBy.get("moneyChangedBy"));
    }

    @Test
    @Transactional
    public void buyNormalItemTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.stationService.createNewStation(UUID.randomUUID());

        Map<String, ?> moneyChangedBy = this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "ROCKET");
        assertEquals(-40, moneyChangedBy.get("moneyChangedBy"));
    }

    @Test
    @Transactional
    public void buyUpgradeItemTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.stationService.createNewStation(UUID.randomUUID());

        Map<String, ?> moneyChangedBy = this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "MINING_1");
        assertEquals(-50, moneyChangedBy.get("moneyChangedBy"));
    }

    @Test
    @Transactional
    public void updateRoundTest() {
        RoundDto dto = new RoundDto();
        dto.roundNumber = 1;
        dto.roundStatus = "started";
        this.gameService.updateRound(dto);

        assertEquals(1, this.gameService.getRoundCount());
        assertEquals("started", this.gameService.getRoundStatus());
    }

    @Test
    @Transactional
    public void getSpecificRoundPlayerBalances() {
        RoundDto round1Start = new RoundDto();
        round1Start.roundNumber = 1; round1Start.roundStatus = "started";
        RoundDto round1End = new RoundDto();
        round1End.roundNumber = 1; round1End.roundStatus = "ended";
        RoundDto round2Start = new RoundDto();
        round2Start.roundNumber = 2; round2Start.roundStatus = "started";
        RoundDto round2End = new RoundDto();
        round2End.roundNumber = 2; round2End.roundStatus = "ended";

        this.gameService.updateRound(round1Start);
        UUID newPlayerId = this.playerService.createPlayer(200);
        this.gameService.updateRound(round1End);
        this.gameService.updateRound(round2Start);
        this.playerService.reduceMoney(newPlayerId, 50);
        this.gameService.updateRound(round2End);

        JSONArray round1 = this.playerService.getPlayerBalancesForRound(1);
        JSONObject playerRound1 = (JSONObject) round1.get(0);
        JSONArray round2 = this.playerService.getPlayerBalancesForRound(2);
        JSONObject playerRound2 = (JSONObject) round2.get(0);

        assertEquals(200, playerRound1.get("balance"));
        assertEquals(150, playerRound2.get("balance"));
    }


}
