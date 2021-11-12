package com.example.trading;

import com.example.trading.item.Item;
import com.example.trading.item.ItemRepository;
import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemServiceTests {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final PlayerService playerService;

    @Autowired
    public ItemServiceTests(ItemService service, ItemRepository repository, PlayerService playerService) {
        this.itemService = service;
        this.itemRepository = repository;
        this.playerService = playerService;
    }

    @Test
    @Transactional
    public void itemCreationTest() {
        UUID newItemId = this.itemService.createItem("Pistol", "Can shoot", "item", 50);
        Optional<Item> item = this.itemRepository.findById(newItemId);
        assertEquals(newItemId, item.get().getItemId());
    }

    @Test
    @Transactional
    public void createItemWithInCorrectTypeTest() {
        assertThrows(
                RuntimeException.class, () -> {
                    UUID newItemId = this.itemService.createItem("Pistol", "Can shoot", "meti", 50);
                }
        );
    }

    @Test
    @Transactional
    public void getItemInformationTest() {
        UUID item1 = this.itemService.createItem("Mini Gun", "Can shoot a lot", "item", 50);

        assertEquals(
                "[{\"price\":50,\"id\":\"Mini Gun\",\"type\":\"ITEM\"}]",
                this.itemService.getItems().toString()
        );
    }

    @Test
    @Transactional
    public void buyNonExistentItemTest() {
        UUID playerId = this.playerService.createPlayer(200);

        assertThrows(
                RuntimeException.class,
                () -> this.itemService.buyItem(playerId, "item which does not exist", 1)
        );
    }

    @Test
    @Transactional
    public void buyItemWithoutEnoughMoneyTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID itemId = this.itemService.createItem("Special Lemonade", "It's special. That's it.", "item",250);

        Integer price = this.itemService.buyItem(playerId, "Special Lemonade", 1);
        assertEquals(-1, price);
    }

    @Test
    @Transactional
    public void buyItemSuccessfullyTest() {
        UUID playerId = this.playerService.createPlayer(200);
        UUID itemId = this.itemService.createItem("A Rock", "Because it's the only thing you can afford.", "item", 2);

        Integer newPlayerMoney = this.itemService.buyItem(playerId, "A Rock", 1);
        assertEquals(198, newPlayerMoney);
    }
}
