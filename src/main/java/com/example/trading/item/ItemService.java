package com.example.trading.item;

import com.example.trading.player.PlayerService;
import com.example.trading.station.PlanetService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;
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

    public int buyItem(UUID transactionId, UUID playerId, UUID robotId, UUID planetId, String itemName, int currentRound) {
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

        } else if (item.get().getItemType() == ItemType.HEALTH || item.get().getItemType() == ItemType.ENERGY) {
            // post to ROBOT/robots/{robot-uuid}/instant-restore
            requestPayload.put("restoration-type", itemName);
            buyResponse = new ResponseEntity<>("robot <uuid> has been fully healed", HttpStatus.OK);
//            buyResponse = new ResponseEntity<>("Request could not be accepted", HttpStatus.BAD_REQUEST);
//            buyResponse = new ResponseEntity<>("Robot not found", HttpStatus.NOT_FOUND);

        } else if (item.get().getItemType() == ItemType.ROBOT) {
            // post to ROBOT/robots
            requestPayload.put("player", playerId);
            requestPayload.put("planet", planetId);
//            requestPayload.put("quantity", quantity);
            buyResponse = new ResponseEntity<>("some big object about the robot", HttpStatus.CREATED);
//            buyResponse = new ResponseEntity<>("Request could not be accepted", HttpStatus.BAD_REQUEST);

        } else {
            // post to ROBOT/robots/{robot-uuid}/upgrades
            requestPayload.put("upgrade-type", itemName);
            requestPayload.put("target-lvl", itemName.substring(itemName.length() - 1));
            buyResponse = new ResponseEntity<>("Energy capacity of robot <uuid> has been upgraded to <new-lvl>", HttpStatus.OK);
//            buyResponse = new ResponseEntity<>("Request could not be accepted", HttpStatus.BAD_REQUEST);
//            buyResponse = new ResponseEntity<>("Robot not found", HttpStatus.NOT_FOUND);
//            buyResponse = new ResponseEntity<>("Upgrade of robot <uuid> rejected. Current lvl of Energy capacity is <current-lvl>.", HttpStatus.CONFLICT);
        }

        if (buyResponse.getStatusCode() != HttpStatus.OK || buyResponse.getStatusCode() != HttpStatus.CREATED) {
            throw new IllegalArgumentException(buyResponse.getBody().toString());
        }

        item.get().addHistory(currentRound);
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

//    public void calculateNewItemPrice(int currentRound) {
//        Iterable<Item> items = this.itemRepository.findAll();
//
//        for (Item item : items) {
//            item.calculateNewPrice(currentRound);
//        }
//
//    }
}
