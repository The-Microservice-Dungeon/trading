package com.example.trading;

import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.ResourceService;
import com.example.trading.station.PlanetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TradingIntegrationTest {

    private final PlayerService playerService;
    private final PlanetService planetService;
    private final ResourceService resourceService;
    private final ItemService itemService;

    @Autowired
    public TradingIntegrationTest(PlayerService playerService,
                                  PlanetService planetService,
                                  ResourceService resourceService,
                                  ItemService itemService) {
        this.playerService = playerService;
        this.planetService = planetService;
        this.resourceService = resourceService;
        this.itemService = itemService;
    }

    @Test
    @Transactional
    public void sellResourceSuccessfullyTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());

        int gotMoney = this.resourceService.sellResources(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId);
        assertTrue(gotMoney >= 0);
    }

    @Test
    @Transactional
    public void buySingleRobotTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetOne = this.planetService.createNewPlanet(UUID.randomUUID());
        UUID planetTwo = this.planetService.createNewPlanet(UUID.randomUUID());

        Integer moneyChangedBy = this.itemService.buyRobots(UUID.randomUUID(), playerId, 1);
        assertEquals(-100, moneyChangedBy);
    }

    @Test
    @Transactional
    public void buyMultipleRobotsTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetOne = this.planetService.createNewPlanet(UUID.randomUUID());
        UUID planetTwo = this.planetService.createNewPlanet(UUID.randomUUID());

        Integer moneyChangedBy = this.itemService.buyRobots(UUID.randomUUID(), playerId, 2);
        assertEquals(-200, moneyChangedBy);
    }

    @Test
    @Transactional
    public void buyNormalItemTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());

        Integer moneyChangedBy = this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "ROCKET");
        assertEquals(-40, moneyChangedBy);
    }

    @Test
    @Transactional
    public void buyUpgradeItemTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());

        Integer moneyChangedBy = this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "MINING_1");
        assertEquals(-50, moneyChangedBy);
    }


}
