package com.example.trading.item;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.example.trading.player.PlayerService;
import com.example.trading.station.StationService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private StationService stationService;

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

    public int buyItem(UUID playerId, String itemName, int currentRound) {
        Optional<Item> item = this.itemRepository.findByName(itemName);
        if (item.isEmpty()) throw new IllegalArgumentException("Item does not exist");

        // check position
//        if (stationService.checkIfGivenPositionIsOneOfTheStations(x , y))
//            return -2;

        if (!this.playerService.checkPlayerForMoney(playerId, item.get().getCurrentPrice()))
            return -1;

        // rest call to robot form buy

        // get response for error
        // return error

        // else

        item.get().addHistory(currentRound);

        return this.playerService.reduceMoney(playerId, item.get().getCurrentPrice());
    }

    public JSONArray getItems() {
        Iterable<Item> items = this.itemRepository.findAll();

        JSONArray itemArray = new JSONArray();

        for (Item item : items) {
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("id", item.getName());
            jsonItem.put("price", item.getCurrentPrice());
            jsonItem.put("type", item.getItemType());
            itemArray.appendElement(jsonItem);
        }

        return itemArray;
    }

    public JSONObject getItem(String name) {
        Optional<Item> item = this.itemRepository.findByName(name);
        if (item.isEmpty()) throw new IllegalArgumentException("Item does not exist");

        JSONObject returnItem = new JSONObject();
        returnItem.put("id", item.get().getName());
        returnItem.put("price", item.get().getCurrentPrice());
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
