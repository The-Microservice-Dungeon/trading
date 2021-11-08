package com.example.trading;

import com.example.trading.item.Item;
import com.example.trading.item.ItemRepository;
import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;

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
        Integer newItemId = this.itemService.createItem("Pistol", "Can shoot", 50);
        Optional<Item> item = this.itemRepository.findById(newItemId);
        assertEquals(newItemId, item.get().getItemId());
    }

    @Test
    @Transactional
    public void getItemListTest() {
        Integer item1 = this.itemService.createItem("Mini Gun", "Can shoot a lot", 50);
        Integer item2 = this.itemService.createItem("Nuke", "I am become death", 200);

        assertEquals(
                "mini gun: 50;\nnuke: 200;\n",
                this.itemService.getItemPriceList()
        );
    }

    @Test
    @Transactional
    public void buyNonExistentItemTest() {
        Integer playerId = this.playerService.createPlayer(200);

        assertThrows(
                RuntimeException.class,
                () -> this.itemService.buyItem(playerId, "item which does not exist", 1)
        );
    }

    @Test
    @Transactional
    public void buyItemWithoutEnoughMoneyTest() {
        Integer playerId = this.playerService.createPlayer(200);
        Integer itemId = this.itemService.createItem("Special Lemonade", "It's special. That's it.", 250);

        Integer price = this.itemService.buyItem(playerId, "Special Lemonade", 1);
        assertEquals(-1, price);
    }

    @Test
    @Transactional
    public void buyItemSuccessfullyTest() {
        Integer playerId = this.playerService.createPlayer(200);
        Integer itemId = this.itemService.createItem("A Rock", "Because it's the only thing you can afford.", 2);

        Integer newPlayerMoney = this.itemService.buyItem(playerId, "A Rock", 1);
        assertEquals(198, newPlayerMoney);
    }
}
