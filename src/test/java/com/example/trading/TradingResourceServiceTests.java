package com.example.trading;

import com.example.trading.item.ItemRepository;
import com.example.trading.item.ItemService;
import com.example.trading.player.Player;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.Resource;
import com.example.trading.resource.ResourceRepository;
import com.example.trading.resource.ResourceService;
import com.example.trading.station.PlanetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TradingResourceServiceTests {
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;
    private final PlayerService playerService;
    private final PlanetService planetService;

    @Autowired
    public TradingResourceServiceTests(ResourceService service, ResourceRepository repository, PlayerService playerService, PlanetService planetService) {
        this.resourceService = service;
        this.resourceRepository = repository;
        this.playerService = playerService;
        this.planetService = planetService;
    }

    @Test
    @Transactional
    public void resourceCreationTest() {
        UUID newResourceId = this.resourceService.createResource("Coal", 5);
        Optional<Resource> resource = this.resourceRepository.findById(newResourceId);
        assertEquals(newResourceId, resource.get().getResourceId());
    }

    @Test
    @Transactional
    public void getResourceInformationTest() {
        UUID resource1 = this.resourceService.createResource("Iron", 10);

        assertEquals(
                "[{\"price\":10,\"name\":\"iron\"}]",
                this.resourceService.getResources().toString()
        );
    }

    @Test
    @Transactional
    public void sellResourceOnNonStationPlanetTest() {
        UUID playerId = this.playerService.createPlayer(200);

        int price = this.resourceService.sellResources(UUID.randomUUID(), playerId, UUID.randomUUID(), UUID.randomUUID());
        assertEquals(-2, price);
    }

    @Test
    @Transactional
    public void sellResourceSuccessfullyTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());
        UUID coal = this.resourceService.createResource("COAL", 5);
        UUID iron = this.resourceService.createResource("IRON", 10);

        // mock data is 5x coal, 2x iron => 45 overall
        Integer newPlayerMoney = this.resourceService.sellResources(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId);
        assertEquals(45, newPlayerMoney);
    }
}
