package com.example.trading;

import com.example.trading.economy.ItemEconomy;
import com.example.trading.item.Item;
import com.example.trading.item.ItemService;
import com.example.trading.item.ItemType;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.Resource;
import com.example.trading.resource.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
public class TradingEconomyTests {
    private final ItemService itemService;
    private final ResourceService resourceService;
    private final PlayerService playerService;

    @Autowired
    public TradingEconomyTests(ItemService itemService, ResourceService resourceService, PlayerService playerService) {
        this.itemService = itemService;
        this.resourceService = resourceService;
        this.playerService = playerService;
    }

    @Test
    @Transactional
    public void calculateNewItemPriceTest() {
        Item item = new Item("Test", "desc", ItemType.ITEM, 10);

        for (int i = 0; i < 150; i++) {
            for (int j = 0; j < 2; j++) {
                item.addHistory(i);
            }
        }

        item.calculateNewPrice(49);
        item.calculateNewPrice(50);
    }

    @Test
    @Transactional
    public void calculateNewResourcePriceTest() {
        Resource resource = new Resource("Test", 20);

        resource.addHistory(9, 1);
        resource.addHistory(10, 2);
        resource.addHistory(0, 3);

        resource.calculateNewPrice(5);
    }
}
