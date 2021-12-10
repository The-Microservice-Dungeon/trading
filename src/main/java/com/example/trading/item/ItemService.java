package com.example.trading.item;

import com.example.trading.player.PlayerService;
import com.example.trading.round.RoundService;
import com.example.trading.station.PlanetService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileReader;
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

    public UUID createItem(String name, String description, String type, int price) {
        ItemType itemType;

        try {
            itemType = ItemType.valueOf(type.toUpperCase());
        } catch (Exception E) {
            throw new IllegalArgumentException("ItemType is not valid");
        }

        Item newItem = new Item(name, description, itemType, price);
        itemRepository.save(newItem);
        return newItem.getItemId();
    }

    public void createItem(ItemDto itemDto) {
        ItemType itemType;

        try {
             itemType = ItemType.valueOf(itemDto.itemType.toUpperCase());
        } catch (Exception E) {
            throw new IllegalArgumentException("Given itemType is not valid");
        }

        Item item = new Item(itemDto.name, itemDto.description, itemType, itemDto.price);
        this.itemRepository.save(item);
    }

    public int buyRobots(UUID transactionId, UUID playerId, int robotAmount) {
        if (robotAmount <= 0)
            throw new IllegalArgumentException("Cannot buy " + robotAmount + " robots");

        Optional<Item> item = this.itemRepository.findByName("ROBOT");
        int fullPrice = item.get().getCurrentPrice() * robotAmount;

        if (!this.playerService.checkPlayerForMoney(playerId, fullPrice))
            throw new IllegalArgumentException("Player '" + playerId + "' does not have enough money");

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("transactionId", transactionId);
        requestPayload.put("player", transactionId);
        requestPayload.put("planets", this.planetService.getRandomPlanets(robotAmount));
        requestPayload.put("quantity", robotAmount);
        ResponseEntity<?> buyResponse;

//        POST zu ROBOT/robots
        buyResponse = new ResponseEntity<>("some big array with created robots", HttpStatus.CREATED);
//            buyResponse = new ResponseEntity<>("Request could not be accepted", HttpStatus.BAD_REQUEST);

        if (buyResponse.getStatusCode() != HttpStatus.CREATED)
            throw new IllegalArgumentException(buyResponse.getBody().toString());

        int newAmount = this.playerService.reduceMoney(playerId, fullPrice);
        return -fullPrice;
    }

    public int buyItem(UUID transactionId, UUID playerId, UUID robotId, UUID planetId, String itemName) {
        Optional<Item> item = this.itemRepository.findByName(itemName);
        if (item.isEmpty())
            throw new IllegalArgumentException("Item '" + itemName + "' does not exist");

        if (!this.planetService.checkIfGivenPlanetIsAStation(planetId))
            throw new IllegalArgumentException("Planet '" + planetId + "' is not a station/spawn");
        if (!this.playerService.checkPlayerForMoney(playerId, item.get().getCurrentPrice()))
            throw new IllegalArgumentException("Player '" + playerId + "' does not have enough money");

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("transaction-id", transactionId);

        ResponseEntity<?> buyResponse;

        if (item.get().getItemType() == ItemType.ITEM) {
            // post to ROBOT/robots/{robot-uuid}/inventory/items
            requestPayload.put("item-type", itemName);
            buyResponse = new ResponseEntity<>("Item <item> added to robot <uuid>.", HttpStatus.OK);
//            buyResponse = new ResponseEntity<>("Request could not be accepted", HttpStatus.BAD_REQUEST);
//            buyResponse = new ResponseEntity<>("Robot not found", HttpStatus.NOT_FOUND);
            item.get().addHistory(this.roundService.getRoundCount());

        } else if (item.get().getItemType() == ItemType.HEALTH || item.get().getItemType() == ItemType.ENERGY) {
            // post to ROBOT/robots/{robot-uuid}/instant-restore
            requestPayload.put("restoration-type", itemName);
            buyResponse = new ResponseEntity<>("robot <uuid> has been fully healed", HttpStatus.OK);
//            buyResponse = new ResponseEntity<>("Request could not be accepted", HttpStatus.BAD_REQUEST);
//            buyResponse = new ResponseEntity<>("Robot not found", HttpStatus.NOT_FOUND);

        } else {
            // post to ROBOT/robots/{robot-uuid}/upgrades
            requestPayload.put("upgrade-type", itemName);
            buyResponse = new ResponseEntity<>("Energy capacity of robot <uuid> has been upgraded to <new-lvl>", HttpStatus.OK);
//            buyResponse = new ResponseEntity<>("Request could not be accepted", HttpStatus.BAD_REQUEST);
//            buyResponse = new ResponseEntity<>("Robot not found", HttpStatus.NOT_FOUND);
//            buyResponse = new ResponseEntity<>("Upgrade of robot <uuid> rejected. Current lvl of Energy capacity is <current-lvl>.", HttpStatus.CONFLICT);
        }

        if (buyResponse.getStatusCode() != HttpStatus.OK)
            throw new IllegalArgumentException(buyResponse.getBody().toString());

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
        if (item.isEmpty()) throw new IllegalArgumentException("Item '" + name + "' does not exist");

        JSONObject returnItem = new JSONObject();
        returnItem.put("item-name", item.get().getName());
        returnItem.put("price", item.get().getCurrentPrice());
        returnItem.put("type", item.get().getItemType().toString().toLowerCase());
        return returnItem;
    }

    public void patchItemEconomyParameters(String name, JSONObject parameters) throws Exception {
        Optional<Item> item = this.itemRepository.findByName(name);
        if (item.isEmpty()) throw new IllegalArgumentException("Item '" + name + "' does not exist");

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
    }

//    @PostConstruct
//    public void createItemsOnStartUp() {
//        JSONParser parser = new JSONParser();
//        try {
//            JSONArray itemArray = (JSONArray) parser.parse(new FileReader("src/main/resources/items.json"));
//
//            for (Object item : itemArray) {
//                JSONObject jsonItem = (JSONObject) item;
//                this.createItem(
//                    jsonItem.get("name").toString(),
//                    jsonItem.get("description").toString(),
//                    jsonItem.get("itemType").toString(),
//                    (int) jsonItem.get("price")
//                );
//            }
//        } catch (Exception e) {
//            System.out.println("Could not find File");
//        }
//    }
//
//    @PreDestroy
//    public void removeItemsOnStop() {
//        this.itemRepository.deleteAll();
//    }
}
