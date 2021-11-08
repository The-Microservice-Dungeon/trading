package com.example.trading;

import com.example.trading.economy.ItemEconomy;
import com.example.trading.item.Item;
import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.Resource;
import com.example.trading.resource.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
public class EconomyTests {
    private final ItemService itemService;
    private final ResourceService resourceService;
    private final PlayerService playerService;

    @Autowired
    public EconomyTests(ItemService itemService, ResourceService resourceService, PlayerService playerService) {
        this.itemService = itemService;
        this.resourceService = resourceService;
        this.playerService = playerService;
    }

    @Test
    @Transactional
    public void calculateNewItemPriceTest() {
        Item item = new Item("Test", "desc", 20);

        for (int i = 0; i < 12; i++) {
            item.addHistory(1);
        }

        item.calculateNewPrice(5);
    }

    @Test
    @Transactional
    public void calculateNewResourcePriceTest() {
        Resource resource = new Resource("Test", 20);

        resource.addHistory(0, 1);
        resource.addHistory(10, 2);
        resource.addHistory(0, 3);

        resource.calculateNewPrice(5);
    }
}
