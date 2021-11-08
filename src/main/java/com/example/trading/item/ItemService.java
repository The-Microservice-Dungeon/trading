package com.example.trading.item;

import com.example.trading.player.PlayerService;
import com.example.trading.station.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public UUID createItem(String name, String description, int price) {
        Item newItem = new Item(name, description, price);
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

    public String getItemPriceList() {
        Iterable<Item> items = this.itemRepository.findAll();
        StringBuilder list = new StringBuilder();

        for (Item item : items) {
            list.append(item.getName())
                .append(": ")
                .append(item.getCurrentPrice())
                .append(";\n");
        }

        return list.toString();
    }

    public void calculateNewItemPrice(int currentRound) {
        Iterable<Item> items = this.itemRepository.findAll();

        for (Item item : items) {
            item.calculateNewPrice(currentRound);
        }

    }
}
