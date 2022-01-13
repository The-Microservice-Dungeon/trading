package com.example.trading.item;

import com.example.trading.core.RestService;
import com.example.trading.core.exceptions.ItemDoesNotExistException;
import com.example.trading.core.exceptions.PlanetIsNotAStationException;
import com.example.trading.core.exceptions.PlayerMoneyTooLowException;
import com.example.trading.core.exceptions.RequestReturnedErrorException;
import com.example.trading.player.PlayerService;
import com.example.trading.game.GameService;
import com.example.trading.resource.Resource;
import com.example.trading.station.StationService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
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

    /**
     * creates item or returns item id if it already exists
     * @param name of item
     * @param description of item
     * @param type of item
     * @param price of item
     * @return uuid of created item
     */
    public UUID createItem(String name, String description, String type, int price) {
        ItemType itemType;

        try {
            itemType = ItemType.valueOf(type.toUpperCase());
        } catch (Exception E) {
            throw new IllegalArgumentException("ItemType is not valid");
        }

        Optional<Item> item= this.itemRepository.findByName(name);
        if (item.isPresent()) return item.get().getItemId();

        Item newItem = new Item(name, description, itemType, price);
        this.itemRepository.save(newItem);
        return newItem.getItemId();
    }

    /**
     * handler for robot buy command
     * does a rest call to the robot-service
     * @param transactionId from the command
     * @param playerId from the command
     * @param robotAmount that should be bought
     * @return amount of money that has been deducted from the player
     */
    public Map<String, ?> buyRobots(UUID transactionId, UUID playerId, int robotAmount) {
        if (robotAmount <= 0)
            throw new IllegalArgumentException("Cannot buy " + robotAmount + " robots");

        Optional<Item> item = this.itemRepository.findByName("ROBOT");
        int fullPrice = item.get().getCurrentPrice() * robotAmount;

        if (!this.playerService.checkPlayerForMoney(playerId, fullPrice))
            throw new PlayerMoneyTooLowException(playerId.toString(), fullPrice);

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("transactionId", transactionId);
        requestPayload.put("player", transactionId);
        requestPayload.put("planets", this.stationService.getRandomStations(robotAmount));
        requestPayload.put("quantity", robotAmount);
        ResponseEntity<?> buyResponse;

        buyResponse = this.restService.post(System.getenv("ROBOT_SERVICE") + "/robots", requestPayload, JSONArray.class);

        if (buyResponse.getStatusCode() != HttpStatus.CREATED)
            throw new RequestReturnedErrorException(buyResponse.getBody().toString());

        int newAmount = this.playerService.reduceMoney(playerId, fullPrice);

        Map<String, String> returnData = new HashMap<>();
        returnData.put("moneyChangedBy", String.valueOf(-fullPrice));
        returnData.put("message", buyResponse.getBody().toString());
        returnData.put("data", buyResponse.getBody().toString());
        return returnData;
    }

    /**
     * handler for item buy command
     * does a rest-calls to the robot-service
     * @param transactionId from the command
     * @param playerId from the command
     * @param robotId from the command
     * @param planetId from the command
     * @param itemName that should be bought
     * @return amount of money that has been deducted from the player
     */
    public Map<String, ?> buyItem(UUID transactionId, UUID playerId, UUID robotId, UUID planetId, String itemName) {
        Optional<Item> item = this.itemRepository.findByName(itemName);
        if (item.isEmpty()) throw new ItemDoesNotExistException(itemName);

        if (!this.stationService.checkIfGivenPlanetIsAStation(planetId))
            throw new PlanetIsNotAStationException(planetId.toString());
        if (!this.playerService.checkPlayerForMoney(playerId, item.get().getCurrentPrice()))
            throw new PlayerMoneyTooLowException(playerId.toString(), item.get().getCurrentPrice());

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("transactionId", transactionId);

        ResponseEntity<?> buyResponse = null;

        if (item.get().getItemType() == ItemType.ITEM) {
            requestPayload.put("itemType", itemName);
            buyResponse = this.restService.post(System.getenv("ROBOT_SERVICE") + "/robots/" + robotId + "/inventory/items", requestPayload, String.class);
            item.get().addHistory(this.gameService.getRoundCount());

        } else if (item.get().getItemType() == ItemType.HEALTH || item.get().getItemType() == ItemType.ENERGY) {
            requestPayload.put("restorationType", itemName);
            buyResponse = this.restService.post(System.getenv("ROBOT_SERVICE") + "/robots/" + robotId + "/instant-restore", requestPayload, String.class);

        } else {
            requestPayload.put("upgradeType", itemName.substring(0, itemName.length() - 1));
            requestPayload.put("targetLevel", itemName.substring(itemName.length() - 1));
            buyResponse = this.restService.post(System.getenv("ROBOT_SERVICE") + "/robots/" + robotId + "/upgrades", requestPayload, String.class);
        }

        if (buyResponse.getStatusCode() != HttpStatus.OK)
            throw new RequestReturnedErrorException(buyResponse.getBody().toString());


        int newAmount = this.playerService.reduceMoney(playerId, item.get().getCurrentPrice());

        Map<String, String> returnData = new HashMap<>();
        returnData.put("moneyChangedBy", String.valueOf(-item.get().getCurrentPrice()));
        returnData.put("message", buyResponse.getBody().toString());
        returnData.put("data", null);
        return returnData;
    }

    /**
     * returns all items with current prices
     * used for the events and rest-calls
     * @return array with items
     */
    public JSONArray getItems() {
        JSONArray itemArray = new JSONArray();

        for (Item item : this.itemRepository.findAll()) {
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("item-name", item.getName());
            jsonItem.put("price", item.getCurrentPrice());
            jsonItem.put("type", item.getItemType().toString().toLowerCase());
            itemArray.appendElement(jsonItem);
        }

        return itemArray;
    }

    /**
     * returns a specific item
     * used for rest-calls
     * @return object with item or exception
     */
    public JSONObject getItem(String name) {
        Optional<Item> item = this.itemRepository.findByName(name);
        if (item.isEmpty()) throw new ItemDoesNotExistException(name);

        JSONObject returnItem = new JSONObject();
        returnItem.put("item-name", item.get().getName());
        returnItem.put("price", item.get().getCurrentPrice());
        returnItem.put("type", item.get().getItemType().toString().toLowerCase());
        return returnItem;
    }

    /**
     * returns all special items with their complete price history
     * used for the according rest-call /items/history/price
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
     * returns all special items with their complete buy history
     * used for the according rest-call /items/history/buy
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
        Iterable<Item> items = this.itemRepository.findAllByItemType(ItemType.ITEM);
        for (Item item : items) {
            item.calculateNewPrice(this.gameService.getRoundCount());
        }

        this.itemEventProducer.publishNewItemPrices(this.itemRepository.findAll().toString());
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
        JSONParser parser = new JSONParser();
        try {
            File file = ResourceUtils.getFile("classpath:items.json");
            InputStream in = new FileInputStream(file);

            JSONArray itemArray = (JSONArray) parser.parse(new InputStreamReader(in, StandardCharsets.UTF_8));

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
            System.out.println("Probably couldn't find file or some duplicate in ItemService: " + e.getMessage());
        }
    }

    @PreDestroy
    public void removeAllItems() {
        this.itemRepository.deleteAll();
    }
}
