package dungeon.trading;

import dungeon.trading.core.exceptions.PlanetIsNotAStationException;
import dungeon.trading.player.PlayerService;
import dungeon.trading.resource.Resource;
import dungeon.trading.resource.ResourceRepository;
import dungeon.trading.resource.ResourceService;
import net.minidev.json.JSONArray;
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

    @Autowired
    public TradingResourceServiceTests(ResourceService service, ResourceRepository repository, PlayerService playerService) {
        this.resourceService = service;
        this.resourceRepository = repository;
        this.playerService = playerService;
    }

    @Test
    @Transactional
    public void resourceCreationTest() {
        UUID newResourceId = this.resourceService.createResource("SOMETHING DIFFERENT", 5);
        Optional<Resource> resource = this.resourceRepository.findById(newResourceId);
        assertEquals(newResourceId, resource.get().getResourceId());
    }

    @Test
    @Transactional
    public void getResourceInformationTest() {
        String resources = this.resourceService.getResources().toString();
        assertNotEquals("[]", resources);
    }

    @Test
    @Transactional
    public void sellResourceOnNonStationPlanetTest() {
        UUID playerId = this.playerService.createPlayer(200);

        assertThrows(
                PlanetIsNotAStationException.class,
                () -> this.resourceService.sellResources(UUID.randomUUID(), playerId, UUID.randomUUID(), UUID.randomUUID())
        );
    }

    @Test
    @Transactional
    public void getResourcePriceHistoryTest() {
        JSONArray priceHistory = this.resourceService.getResourcePriceHistory();
        assertNotEquals("[]", priceHistory.toString());
    }

    @Test
    @Transactional
    public void getResourceSellHistoryTest() {
        JSONArray sellHistory = this.resourceService.getResourceSellHistory();
        assertNotEquals("[]", sellHistory.toString());
    }
}
