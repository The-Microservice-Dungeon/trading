package com.example.trading;

import com.example.trading.item.ItemRepository;
import com.example.trading.item.ItemService;
import com.example.trading.player.Player;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.Resource;
import com.example.trading.resource.ResourceRepository;
import com.example.trading.resource.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ResourceServiceTests {
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;
    private final PlayerService playerService;

    @Autowired
    public ResourceServiceTests(ResourceService service, ResourceRepository repository, PlayerService playerService) {
        this.resourceService = service;
        this.resourceRepository = repository;
        this.playerService = playerService;
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
    public void resourceItemListTest() {
        UUID resource1 = this.resourceService.createResource("Iron", 10);
        UUID resource2 = this.resourceService.createResource("Gold", 20);

        assertEquals(
                "iron: 10;\ngold: 20;\n",
                this.resourceService.getResourcePriceList()
        );
    }

    @Test
    @Transactional
    public void sellNonExistentResourceTest() {
        UUID playerId = this.playerService.createPlayer(200);

        assertThrows(
                RuntimeException.class,
                () -> this.resourceService.sellResource(playerId, "resource which does not exist", 100, 1)
        );
    }

    @Test
    @Transactional
    public void sellResourceSuccessfullyTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID resourceId = this.resourceService.createResource("Diamond", 100);

        Integer newPlayerMoney = this.resourceService.sellResource(playerId, "Diamond", 2, 1);
        assertEquals(400, newPlayerMoney);
    }
}
