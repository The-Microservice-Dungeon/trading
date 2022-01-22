package dungeon.trading;

import dungeon.trading.core.exceptions.ItemDoesNotExistException;
import dungeon.trading.core.exceptions.PlanetIsNotAStationException;
import dungeon.trading.core.exceptions.PlayerMoneyTooLowException;
import dungeon.trading.item.Item;
import dungeon.trading.item.ItemRepository;
import dungeon.trading.item.ItemService;
import dungeon.trading.player.PlayerService;
import dungeon.trading.station.StationService;
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
    private final StationService stationService;

    @Autowired
    public TradingItemServiceTests(ItemService service, ItemRepository repository, PlayerService playerService, StationService stationService) {
        this.itemService = service;
        this.itemRepository = repository;
        this.playerService = playerService;
        this.stationService = stationService;
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
        UUID planetId = this.stationService.createNewStation(UUID.randomUUID());

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
        UUID planetId = this.stationService.createNewStation(UUID.randomUUID());

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

    @Test
    @Transactional
    public void getItemPriceHistoryTest() {
        JSONArray items = this.itemService.getItemPriceHistory();
        assertNotEquals("[]", items.toString());
    }

    @Test
    @Transactional
    public void getItemBuyHistoryTest() {
        JSONArray items = this.itemService.getItemBuyHistory();
        assertNotEquals("[]", items.toString());
    }
}
