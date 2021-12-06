package com.example.trading;

import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.ResourceService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
public class TradingController {
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ItemService itemService;

    @GetMapping("/resources")
    public ResponseEntity<?> getInformationAboutAllResources() {
        JSONArray resources = this.resourceService.getResources();
        return new ResponseEntity<JSONArray>(resources, HttpStatus.OK);
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

    @GetMapping("/items")
    public ResponseEntity<?> getInformationAboutAllItems() {
        JSONArray items = itemService.getItems();
        return new ResponseEntity<JSONArray>(items, HttpStatus.OK);
    }

    @GetMapping("/balances")
    public ResponseEntity<?> getAllPlayerBalances() {
        JSONArray balances = this.playerService.getAllPlayerBalances();
        return new ResponseEntity<JSONArray>(balances, HttpStatus.OK);
    }

    @PostMapping("/commands")
    public ResponseEntity<?> processInComingTradingCommands(@RequestBody String commands) {
        JSONParser parser = new JSONParser();
        JSONArray commandsArray = new JSONArray();
        try {
            commandsArray = (JSONArray) parser.parse(commands);
        } catch (Exception e) {
            System.out.println("Cant Parse String: " + e.getMessage());
        }
        JSONObject response = new JSONObject();

        for (int i = 0; i < commandsArray.size(); i++) {
            JSONObject command = (JSONObject) commandsArray.get(i);
            JSONObject payload = (JSONObject) command.get("payload");

            int moneyChangedBy = 0;

            response.put("transactionId", command.get("transactionId"));

            if (Objects.equals(payload.get("commandType"), "buy")) {
                String item = null;

                try {
                    item = (String) payload.get("itemName");
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("moneyChangedBy", 0);
                    response.put("message", e.getMessage());
//                    kafka produce
                }

                if (Objects.equals(item, "ROBOT")) {
                    try {
                        moneyChangedBy = this.itemService.buyRobots(
                                UUID.fromString((String) command.get("transactionId")),
                                UUID.fromString((String) command.get("playerId")),
                                (Integer) payload.get("amount")
                        );
                    } catch (Exception e) {
                        response.put("success", false);
                        response.put("moneyChangedBy", 0);
                        response.put("message", e.getMessage());
//                        kafka Produce
                    }

                } else if (item != null) {
                    try {
                        moneyChangedBy = this.itemService.buyItem(
                                UUID.fromString((String) command.get("transactionId")),
                                UUID.fromString((String) command.get("playerId")),
                                UUID.fromString((String) payload.get("robotId")),
                                UUID.fromString((String) payload.get("planetId")),
                                (String) payload.get("itemName")
                        );
                    } catch (Exception e) {
                        response.put("success", false);
                        response.put("moneyChangedBy", 0);
                        response.put("message", e.getMessage());
//                    kafka Produce
                    }

                } else {
                    System.out.println("itemName not given");
                }

            } else if (Objects.equals(payload.get("commandType"), "sell")) {
                try {
                    moneyChangedBy = this.resourceService.sellResources(
                            UUID.fromString((String) command.get("transactionId")),
                            UUID.fromString((String) command.get("playerId")),
                            UUID.fromString((String) payload.get("robotId")),
                            UUID.fromString((String) payload.get("planetId"))
                    );
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("moneyChangedBy", 0);
                    response.put("message", e.getMessage());
//                    kafka Produce
                }
            }

            response.put("success", true);
            response.put("moneyChangedBy", moneyChangedBy);
            response.put("message", "success");
//            Kafka produce
        }

//        preis berechnung
//        event mit neuen preisen

        return new ResponseEntity<>(response, HttpStatus.OK);
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

    @PatchMapping("/resources/{resource-name}/economy")
    public ResponseEntity<?> patchResourceEconomyParameters(@PathVariable("resource-name") String resourceName, @RequestBody String newParameters) {
        JSONParser parser = new JSONParser();
        JSONObject parameters = new JSONObject();
        try {
            parameters = (JSONObject) parser.parse(newParameters);
        } catch (Exception e) {
            System.out.println("Cant Parse String: " + e.getMessage());
        }

        try {
            this.resourceService.patchItemEconomyParameters(resourceName, parameters);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
