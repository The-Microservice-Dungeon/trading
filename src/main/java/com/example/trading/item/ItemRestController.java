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
        JSONArray items = itemService.getItems();
        return new ResponseEntity<JSONArray>(items, HttpStatus.OK);
    }

    @GetMapping("/items/{item-name}")
    public ResponseEntity<?> getInformationAboutOneItem(@PathVariable("item-name") String itemId) {
        try {
            JSONObject foundItem = itemService.getItem(itemId);
            return new ResponseEntity<JSONObject>(foundItem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/items/{item-name}/economy")
    public ResponseEntity<?> patchItemEconomyParameters(@PathVariable("item-name") String itemName, @RequestBody String newParameters) {
        JSONParser parser = new JSONParser();
        JSONObject parameters = new JSONObject();
        try {
            parameters = (JSONObject) parser.parse(newParameters);
        } catch (Exception e) {
            System.out.println("Cant Parse String: " + e.getMessage());
        }

        try {
            this.itemService.patchItemEconomyParameters(itemName, parameters);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
