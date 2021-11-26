package com.example.trading;

import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.ResourceService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
//        Autentifizierung
        JSONArray balances = this.playerService.getAllPlayerBalances();
        return new ResponseEntity<JSONArray>(balances, HttpStatus.OK);
    }

    @PostMapping("/commands")
    public ResponseEntity<?> processInComingTradingCommands(@RequestBody JSONArray commands) {
        for (int i = 0; i < commands.size(); i++) {
            JSONObject command = (JSONObject) commands.get(i);
            JSONObject payload = (JSONObject) command.get("payload");
            int moneyChangeBy = 0;

            JSONObject response = new JSONObject();
            response.put("transactionId", command.get("transactionId"));

            if (payload.get("commandType") == "buy") {
                try {
                    moneyChangeBy = this.itemService.buyItem(
                            UUID.fromString((String) command.get("transactionId")),
                            UUID.fromString((String) command.get("playerId")),
                            UUID.fromString((String) payload.get("robotId")),
                            UUID.fromString((String) payload.get("planetId")),
                            (String) payload.get("itemName"),
//                            (Integer) payload.get("quantity"),
                            1
                    );
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("moneyChangedBy", 0);
                    response.put("message", e.getMessage());
//                    kafka Produce
                }

            } else if (payload.get("commandType") == "sell") {
                try {
                    moneyChangeBy = this.resourceService.sellResources(
                            UUID.fromString((String) command.get("transactionId")),
                            UUID.fromString((String) command.get("playerId")),
                            UUID.fromString((String) payload.get("robotId")),
                            UUID.fromString((String) payload.get("planetId")),
                            1
                    );
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("moneyChangedBy", 0);
                    response.put("message", e.getMessage());
//                    kafka Produce
                }
            }

            response.put("success", true);
            response.put("moneyChangedBy", moneyChangeBy);
            response.put("message", "success");
//            Kafka produce
        }

//        return? just a success?
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
