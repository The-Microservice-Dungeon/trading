package dungeon.trading;

import dungeon.trading.item.Item;
import dungeon.trading.item.ItemService;
import dungeon.trading.item.ItemType;
import dungeon.trading.player.PlayerService;
import dungeon.trading.resource.Resource;
import dungeon.trading.resource.ResourceService;
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
