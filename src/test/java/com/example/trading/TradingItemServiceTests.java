package com.example.trading;

import com.example.trading.item.Item;
import com.example.trading.item.ItemRepository;
import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import com.example.trading.station.PlanetService;
import net.minidev.json.JSONArray;
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
                    UUID newItemId = this.itemService.createItem("PISTOL", "Can shoot", "meti", 50);
                }
        );
    }

    @Test
    @Transactional
    public void getAllItemInformationTest() {
        UUID item1 = this.itemService.createItem("MINI GUN", "Can shoot a lot", "item", 50);

        assertEquals(
                "[{\"price\":50,\"item-name\":\"MINI GUN\",\"type\":\"item\"}]",
                this.itemService.getItems().toString()
        );
    }

    @Test
    @Transactional
    public void buyNonExistentItemTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID(), "station");

        assertThrows(
                RuntimeException.class,
                () -> this.itemService.buyItem(
                        UUID.randomUUID(), // transaction
                        playerId,
                        UUID.randomUUID(), // robot
                        planetId,
                        "non existant Item",
                        1
                )
        );
    }

    @Test
    @Transactional
    public void buyItemFromNonStationPlanetTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID(), "planet");
        UUID newItemId = this.itemService.createItem("PISTOL", "Can shoot", "item", 50);

        int price = this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "PISTOL", 1);
        assertEquals(-2, price);
    }

    @Test
    @Transactional
    public void buyItemWithoutEnoughMoneyTest() {
        UUID playerId = this.playerService.createPlayer(40);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID(), "station");
        UUID newItemId = this.itemService.createItem("PISTOL", "Can shoot", "item", 50);

        int price = this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "PISTOL", 1);
        assertEquals(-1, price);
    }

    @Test
    @Transactional
    public void buyItemSuccessfullyTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID(), "station");
        UUID itemId = this.itemService.createItem("A ROCK", "Because it's the only thing you can afford.", "item", 2);

        Integer newPlayerMoney = this.itemService.buyItem(UUID.randomUUID(), playerId, UUID.randomUUID(), planetId, "A ROCK", 1);
        assertEquals(198, newPlayerMoney);
    }
}
