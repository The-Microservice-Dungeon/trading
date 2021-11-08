package com.example.trading;

import com.example.trading.economy.ItemEconomy;
import com.example.trading.item.Item;
import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
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
//        itemService.calculateNewItemPrice(1);
        ItemEconomy economy = new ItemEconomy();
        economy.addHistory(1, 1);
        economy.addHistory(1, 1);
        economy.addHistory(2, 5);
        economy.addHistory(3, 6);
        economy.addHistory(4, 3);
        economy.addHistory(5, 4);

        System.out.println(economy.calculateNewPrice(5));
    }

    @Test
    @Transactional
    public void calculateNewResourcePriceTest() {

    }
}
