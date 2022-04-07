package dungeon.trading.item;

import dungeon.trading.core.RestService;
import dungeon.trading.core.exceptions.ItemDoesNotExistException;
import dungeon.trading.core.exceptions.PlanetIsNotAStationException;
import dungeon.trading.core.exceptions.PlayerMoneyTooLowException;
import dungeon.trading.core.exceptions.RequestReturnedErrorException;
import dungeon.trading.game.GameService;
import dungeon.trading.player.PlayerService;
import dungeon.trading.station.StationService;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
@Slf4j
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private StationService stationService;

    @Autowired
    private GameService gameService;

    @Autowired
    private RestService restService;

    @Autowired
    private ItemEventProducer itemEventProducer;

    @Value("${dungeon.services.robot}")
    private String robotService;

    /**
     * creates item or returns item id if it already exists
     *
     * @param name        of item
     * @param description of item
     * @param type        of item
     * @param price       of item
     * @return uuid of created item
     */
    public UUID createItem(String name, String description, String type, int price) {
        ItemType itemType;

        try {
            itemType = ItemType.valueOf(type.toUpperCase());
        } catch (Exception E) {
            throw new IllegalArgumentException("ItemType is not valid");
        }

        Optional<Item> item = this.itemRepository.findByName(name);
        if (item.isPresent())
            return item.get().getItemId();

        Item newItem = new Item(name, description, itemType, price);
        this.itemRepository.save(newItem);
        return newItem.getItemId();
    }

    /**
     * handler for robot buy command does a rest call to the robot-service
     *
     * @param transactionId from the command
     * @param playerId      from the command
     * @param robotAmount   that should be bought
     * @return amount of money that has been deducted from the player
     */
    public Map<String, Object> buyRobots(UUID transactionId, UUID playerId, int robotAmount) {
        if (robotAmount <= 0)
            throw new IllegalArgumentException("Cannot buy " + robotAmount + " robots");

        Optional<Item> item = this.itemRepository.findByName("ROBOT");
        int fullPrice = item.orElseThrow(() -> new RuntimeException("Could not find Robot Item"))
            .getCurrentPrice() * robotAmount;

        if (!this.playerService.checkPlayerForMoney(playerId, fullPrice))
            throw new PlayerMoneyTooLowException(playerId.toString(), fullPrice);

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("transactionId", transactionId);
        requestPayload.put("player", playerId);
        requestPayload.put("planets", this.stationService.getRandomStations(robotAmount));
        requestPayload.put("quantity", robotAmount);
        ResponseEntity<?> buyResponse;

        buyResponse = this.restService.post(this.robotService + "/robots", requestPayload,
            JSONArray.class);

        if (buyResponse.getStatusCode() != HttpStatus.CREATED) {
            var responseBody =
                buyResponse.getBody() == null ? "" : buyResponse.getBody().toString();
            var errorMessage = MessageFormat.format(
                "Robot Service responded with an invalid response. Status Code: {}, Body: {}",
                buyResponse.getStatusCode(), responseBody);
            throw new RequestReturnedErrorException(errorMessage);
        }

        this.playerService.reduceMoney(playerId, fullPrice);

        Map<String, Object> returnData = new HashMap<>();
        returnData.put("moneyChangedBy", -fullPrice);
        returnData.put("message", "robot-buy successful");
        returnData.put("data", buyResponse.getBody());
        return returnData;
    }

    /**
     * handler for item buy command does a rest-calls to the robot-service
     *
     * @param transactionId from the command
     * @param playerId      from the command
     * @param robotId       from the command
     * @param planetId      from the command
     * @param itemName      that should be bought
     * @return amount of money that has been deducted from the player
     */
    public Map<String, ?> buyItem(UUID transactionId, UUID playerId, UUID robotId, UUID planetId,
        String itemName) {
        Item item = this.itemRepository.findByName(itemName)
            .orElseThrow(() -> new ItemDoesNotExistException(itemName));

        if (!this.stationService.checkIfGivenPlanetIsAStation(planetId))
            throw new PlanetIsNotAStationException(planetId.toString());
        if (!this.playerService.checkPlayerForMoney(playerId, item.getCurrentPrice()))
            throw new PlayerMoneyTooLowException(playerId.toString(), item.getCurrentPrice());

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("transactionId", transactionId);

        ResponseEntity<?> buyResponse = null;

        if (item.getItemType() == ItemType.ITEM) {
            requestPayload.put("itemType", itemName);
            buyResponse = this.restService.post(
                this.robotService + "/robots/" + robotId + "/inventory/items", requestPayload,
                String.class);
            item.addHistory(this.gameService.getRoundCount());

        } else if (item.getItemType() == ItemType.HEALTH || item.getItemType() == ItemType.ENERGY) {
            requestPayload.put("restorationType", itemName);
            buyResponse = this.restService.post(
                this.robotService + "/robots/" + robotId + "/instant-restore", requestPayload,
                String.class);

        } else {
            requestPayload.put("upgradeType", itemName.substring(0, itemName.length() - 1));
            requestPayload.put("targetLevel", itemName.substring(itemName.length() - 1));
            buyResponse = this.restService.post(
                this.robotService + "/robots/" + robotId + "/upgrades", requestPayload,
                String.class);
        }

        if (buyResponse.getStatusCode() != HttpStatus.OK)
            throw new RequestReturnedErrorException(buyResponse.getBody().toString());

        int newAmount = this.playerService.reduceMoney(playerId, item.getCurrentPrice());

        Map<String, Object> returnData = new HashMap<>();
        returnData.put("moneyChangedBy", -item.getCurrentPrice());
        returnData.put("message", buyResponse.getBody());
        returnData.put("data", null);
        return returnData;
    }

    /**
     * returns all items with current prices used for the events and rest-calls
     *
     * @return array with items
     */
    public JSONArray getItems() {
        JSONArray itemArray = new JSONArray();

        for (Item item : this.itemRepository.findAll()) {
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("name", item.getName());
            jsonItem.put("price", item.getCurrentPrice());
            itemArray.appendElement(jsonItem);
        }

        return itemArray;
    }

    /**
     * returns a specific item used for rest-calls
     *
     * @return object with item or exception
     */
    public JSONObject getItem(String name) {
        Item item = this.itemRepository.findByName(name)
            .orElseThrow(() -> new ItemDoesNotExistException(name));

        JSONObject returnItem = new JSONObject();
        returnItem.put("itemName", item.getName());
        returnItem.put("price", item.getCurrentPrice());
        returnItem.put("type", item.getItemType().toString().toLowerCase());
        return returnItem;
    }

    /**
     * returns all special items with their complete price history used for the according rest-call
     * /items/history/price
     *
     * @return JSONArray
     */
    public JSONArray getItemPriceHistory() {
        JSONArray itemArray = new JSONArray();

        for (Item item : this.itemRepository.findAllByItemType(ItemType.ITEM)) {
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("name", item.getName());
            jsonItem.put("history", item.getPriceHistory());
            itemArray.appendElement(jsonItem);
        }

        return itemArray;
    }

    /**
     * returns all special items with their complete buy history used for the according rest-call
     * /items/history/buy
     *
     * @return JSONArray
     */
    public JSONArray getItemBuyHistory() {
        JSONArray itemArray = new JSONArray();

        for (Item item : this.itemRepository.findAllByItemType(ItemType.ITEM)) {
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("name", item.getName());
            jsonItem.put("history", item.getBuyHistory());
            itemArray.appendElement(jsonItem);
        }

        return itemArray;
    }

    /**
     * calculates the new item prices and emits them as an event
     */
    public void calculateNewItemPrices() {
        JSONArray items = new JSONArray();

        for (Item item : this.itemRepository.findAllByItemType(ItemType.ITEM)) {
            item.calculateNewPrice(this.gameService.getRoundCount());

            JSONObject jsonItem = new JSONObject();
            jsonItem.put("name", item.getName());
            jsonItem.put("price", item.getCurrentPrice());
            items.add(jsonItem);
        }

        this.itemEventProducer.publishNewItemPrices(items);
    }

    /**
     *
     */
    public void resetItems() {
        this.removeAllItems();
        this.createAllItems();
    }

    /**
     * creates all items on start up
     */
    @PostConstruct
    public void createAllItems() {
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        try (var in = new FileInputStream(ResourceUtils.getFile("classpath:items.json"))) {
            JSONArray itemArray = (JSONArray) parser.parse(
                new InputStreamReader(in, StandardCharsets.UTF_8));

            for (Object item : itemArray) {
                JSONObject jsonItem = (JSONObject) item;
                this.createItem(
                    jsonItem.get("name").toString(),
                    jsonItem.get("description").toString(),
                    jsonItem.get("itemType").toString(),
                    (int) jsonItem.get("price")
                );
            }
        } catch (Exception e) {
            log.error("Probably couldn't find file or some duplicate in ItemService", e);
        }
    }

    @PreDestroy
    public void removeAllItems() {
        this.itemRepository.deleteAll();
    }
}
