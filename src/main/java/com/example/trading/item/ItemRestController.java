package com.example.trading.item;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ItemRestController {
    @Autowired
    private ItemService itemService;

    @GetMapping("/items")
    public ResponseEntity<?> getInformationAboutAllItems() {
        JSONArray items = this.itemService.getItems();
        return new ResponseEntity<JSONArray>(items, HttpStatus.OK);
    }

    @GetMapping("/items/{item-name}")
    public ResponseEntity<?> getInformationAboutOneItem(@PathVariable("item-name") String itemId) {
        try {
            JSONObject foundItem = this.itemService.getItem(itemId);
            return new ResponseEntity<JSONObject>(foundItem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/items/history/price")
    public ResponseEntity<?> getItemPriceHistory() {
        JSONArray items = this.itemService.getItemPriceHistory();
        return new ResponseEntity<JSONArray>(items, HttpStatus.OK);
    }

    @GetMapping("/items/history/buy")
    public ResponseEntity<?> getItemBuyHistory() {
        JSONArray items = this.itemService.getItemBuyHistory();
        return new ResponseEntity<JSONArray>(items, HttpStatus.OK);
    }
}
