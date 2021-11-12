package com.example.trading.item;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> getInformationAboutOneItem(@PathVariable String itemId) {
        try {
            JSONObject foundItem = itemService.getItem(itemId);
            return new ResponseEntity<JSONObject>(foundItem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/item")
    public ResponseEntity<?> getInformationAboutAllItems() {
        JSONArray items = itemService.getItems();
        return new ResponseEntity<JSONArray>(items, HttpStatus.OK);
    }

//    @PostMapping("/item/{itemId}")
//    public ResponseEntity<?> buyItem(@PathVariable String itemId) {
//        int newMoneyAmount = itemService.buyItem();
//    }
}
