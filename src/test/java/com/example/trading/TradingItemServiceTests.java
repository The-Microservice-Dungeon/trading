package com.example.trading;

import com.example.trading.item.Item;
import com.example.trading.item.ItemRepository;
import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import com.example.trading.station.PlanetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TradingItemServiceTests {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final PlayerService playerService;
    private final PlanetService planetService;

    @Autowired
    public TradingItemServiceTests(ItemService service, ItemRepository repository, PlayerService playerService, PlanetService planetService) {
        this.itemService = service;
        this.itemRepository = repository;
        this.playerService = playerService;
        this.planetService = planetService;
    }

    @Test
    @Transactional
    public void itemCreationTest() {
        UUID newItemId = this.itemService.createItem("PISTOL", "Can shoot", "item", 50);
        Optional<Item> item = this.itemRepository.findById(newItemId);
        assertEquals(newItemId, item.get().getItemId());
    }

    @Test
    @Transactional
    public void createItemWithInCorrectTypeTest() {
        assertThrows(
                RuntimeException.class, () -> {
                    UUID newItemId = this.itemService.createItem("ROCKET", "Can shoot", "meti", 50);
                }
        );
    }

    @Test
    @Transactional
    public void getAllItemInformationTest() {
        String items = this.itemService.getItems().toString();
        assertNotEquals("[]", items);
    }

    @Test
    @Transactional
    public void buyNonExistentItemTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());

        assertThrows(
                RuntimeException.class,
                () -> this.itemService.buyItem(
                        UUID.randomUUID(), // transaction
                        playerId,
                        UUID.randomUUID(), // robot
                        planetId,
                        "non existant Item"
                )
        );
    }

    @Test
    @Transactional
    public void buyItemFromNonStationPlanetTest() {
        UUID playerId = this.playerService.createPlayer(200);

        assertThrows(
            RuntimeException.class,
            () -> this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), UUID.randomUUID(), "ROCKET")
        );
    }

    @Test
    @Transactional
    public void buyItemWithoutEnoughMoneyTest() {
        UUID playerId = this.playerService.createPlayer(5);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());

        assertThrows(
            RuntimeException.class,
            () -> this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "ROCKET")
        );
    }

    @Test
    @Transactional
    public void buyNormalItemTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());

        Integer moneyChangedBy = this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "ROCKET");
        assertEquals(-10, moneyChangedBy);
    }

    @Test
    @Transactional
    public void buyUpgradeItemTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());

        Integer moneyChangedBy = this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "MINING_1");
        assertEquals(-50, moneyChangedBy);
    }

    @Test
    @Transactional
    public void buyNegativeAmountOfRobotsTest() {
        assertThrows(
                RuntimeException.class,
                () -> this.itemService.buyRobots(UUID.randomUUID(), UUID.randomUUID(), -1312)
        );
    }

    @Test
    @Transactional
    public void buyRobotsWithoutEnoughMoneyTest() {
        UUID playerId = this.playerService.createPlayer(50);

        assertThrows(
                RuntimeException.class,
                () -> this.itemService.buyRobots(UUID.randomUUID(), playerId, 2)
        );
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
}
