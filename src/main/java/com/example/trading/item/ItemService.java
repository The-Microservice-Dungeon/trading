package com.example.trading.item;

import com.example.trading.RestService;
import com.example.trading.core.exceptions.ItemDoesNotExistException;
import com.example.trading.core.exceptions.PlanetIsNotAStationException;
import com.example.trading.core.exceptions.PlayerMoneyTooLowException;
import com.example.trading.core.exceptions.RequestReturnedErrorException;
import com.example.trading.player.PlayerService;
import com.example.trading.round.RoundService;
import com.example.trading.station.PlanetService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlanetService planetService;

    @Autowired
    private RoundService roundService;

    @Autowired
    private RestService restService;

    private ItemEventProducer itemEventProducer;

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

    public int buyRobots(UUID transactionId, UUID playerId, int robotAmount) {
        if (robotAmount <= 0)
            throw new IllegalArgumentException("Cannot buy " + robotAmount + " robots");

        Optional<Item> item = this.itemRepository.findByName("ROBOT");
        int fullPrice = item.get().getCurrentPrice() * robotAmount;

        if (!this.playerService.checkPlayerForMoney(playerId, fullPrice))
            throw new PlayerMoneyTooLowException(playerId.toString(), fullPrice);

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("transactionId", transactionId);
        requestPayload.put("player", transactionId);
        requestPayload.put("planets", this.planetService.getRandomPlanets(robotAmount));
        requestPayload.put("quantity", robotAmount);
        ResponseEntity<?> buyResponse;

        buyResponse = this.restService.post(System.getenv("ROBOT_SERVICE") + "/robots", requestPayload, JSONArray.class);
//        buyResponse = new ResponseEntity<>("some big array with created robots", HttpStatus.CREATED);

        if (buyResponse.getStatusCode() != HttpStatus.CREATED)
            throw new RequestReturnedErrorException(buyResponse.getBody().toString());

        int newAmount = this.playerService.reduceMoney(playerId, fullPrice);
        return -fullPrice;
    }

    public int buyItem(UUID transactionId, UUID playerId, UUID robotId, UUID planetId, String itemName) {
        Optional<Item> item = this.itemRepository.findByName(itemName);
        if (item.isEmpty()) throw new ItemDoesNotExistException(itemName);

        if (!this.planetService.checkIfGivenPlanetIsAStation(planetId))
            throw new PlanetIsNotAStationException(planetId.toString());
        if (!this.playerService.checkPlayerForMoney(playerId, item.get().getCurrentPrice()))
            throw new PlayerMoneyTooLowException(playerId.toString(), item.get().getCurrentPrice());

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("transaction-id", transactionId);

        ResponseEntity<?> buyResponse = null;

        if (item.get().getItemType() == ItemType.ITEM) {
            requestPayload.put("item-type", itemName);
            buyResponse = this.restService.post(System.getenv("ROBOT_SERVICE") + "/robots/" + robotId + "/inventory/items", requestPayload, String.class);
//            buyResponse = new ResponseEntity<>("Item <item> added to robot <uuid>.", HttpStatus.OK);
            item.get().addHistory(this.roundService.getRoundCount());

        } else if (item.get().getItemType() == ItemType.HEALTH || item.get().getItemType() == ItemType.ENERGY) {
            requestPayload.put("restoration-type", itemName);
            buyResponse = this.restService.post(System.getenv("ROBOT_SERVICE") + "/robots/" + robotId + "/instant-restore", requestPayload, String.class);
//            buyResponse = new ResponseEntity<>("robot <uuid> has been fully healed", HttpStatus.OK);

        } else {
            requestPayload.put("upgrade-type", itemName);
            buyResponse = this.restService.post(System.getenv("ROBOT_SERVICE") + "/robots/" + robotId + "/upgrades", requestPayload, String.class);
//            buyResponse = new ResponseEntity<>("Energy capacity of robot <uuid> has been upgraded to <new-lvl>", HttpStatus.OK);
        }

        if (buyResponse.getStatusCode() != HttpStatus.OK)
            throw new RequestReturnedErrorException(buyResponse.getBody().toString());

        int newAmount = this.playerService.reduceMoney(playerId, item.get().getCurrentPrice());
        return -item.get().getCurrentPrice();
    }

    public JSONArray getItems() {
        Iterable<Item> items = this.itemRepository.findAll();

        JSONArray itemArray = new JSONArray();

        for (Item item : items) {
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("item-name", item.getName());
            jsonItem.put("price", item.getCurrentPrice());
            jsonItem.put("type", item.getItemType().toString().toLowerCase());
            itemArray.appendElement(jsonItem);
        }

        return itemArray;
    }

    public JSONObject getItem(String name) {
        Optional<Item> item = this.itemRepository.findByName(name);
        if (item.isEmpty()) throw new ItemDoesNotExistException(name);

        JSONObject returnItem = new JSONObject();
        returnItem.put("item-name", item.get().getName());
        returnItem.put("price", item.get().getCurrentPrice());
        returnItem.put("type", item.get().getItemType().toString().toLowerCase());
        return returnItem;
    }

    public void patchItemEconomyParameters(String name, JSONObject parameters) throws Exception {
        Optional<Item> item = this.itemRepository.findByName(name);
        if (item.isEmpty()) throw new ItemDoesNotExistException(name);

        try {
            item.get().changeEconomyParameters(
                    (Integer) parameters.get("roundCount"),
                    (Integer) parameters.get("stock")
            );
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void calculateNewItemPrices() {
        Iterable<Item> items = this.itemRepository.findAllByItemType(ItemType.ITEM);
        for (Item item : items) {
            item.calculateNewPrice(this.roundService.getRoundCount());
        }

        this.itemEventProducer.publishNewItemPrices(this.itemRepository.findAll().toString());
    }

    @PostConstruct
    public void createItemsOnStartUp() {
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
    public void removeItemsOnStop() {
        this.itemRepository.deleteAll();
    }
}
