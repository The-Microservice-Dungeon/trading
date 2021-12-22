package com.example.trading;

import com.example.trading.core.exceptions.ItemDoesNotExistException;
import com.example.trading.core.exceptions.PlanetIsNotAStationException;
import com.example.trading.core.exceptions.PlayerMoneyTooLowException;
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
                ItemDoesNotExistException.class,
                () -> this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "non existant Item")
        );
    }

    @Test
    @Transactional
    public void buyItemFromNonStationPlanetTest() {
        UUID playerId = this.playerService.createPlayer(200);

        assertThrows(
            PlanetIsNotAStationException.class,
            () -> this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), UUID.randomUUID(), "ROCKET")
        );
    }

    @Test
    @Transactional
    public void buyItemWithoutEnoughMoneyTest() {
        UUID playerId = this.playerService.createPlayer(5);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());

        assertThrows(
            PlayerMoneyTooLowException.class,
            () -> this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "ROCKET")
        );
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
                PlayerMoneyTooLowException.class,
                () -> this.itemService.buyRobots(UUID.randomUUID(), playerId, 2)
        );
    }


}
