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
    private ItemService itemService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TradingEventProducer tradingEventProducer;

    /**
     * main post controller for commands that have to be handled in trading
     * @param commands requestbody of json-string-array with commands
     * @return 200 Ok
     */
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

        for (Object commandObject : commandsArray) {
            JSONObject command = (JSONObject) commandObject;
            JSONObject payload = (JSONObject) command.get("payload");

            int moneyChangedBy = 0;

            String transactionId = command.get("transactionId").toString();
            String eventType = "";

            if (Objects.equals(payload.get("commandType"), "buy")) {
                String item = null;

                try {
                    item = (String) payload.get("itemName");
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("moneyChangedBy", 0);
                    response.put("message", e.getMessage());
//                    kafka produce
                    this.tradingEventProducer.publishTradingResult(response.toString(), transactionId, "buy-error");
                    continue;
                }

                if (Objects.equals(item, "ROBOT")) {
                    try {
                        moneyChangedBy = this.itemService.buyRobots(
                                UUID.fromString((String) command.get("transactionId")),
                                UUID.fromString((String) command.get("playerId")),
                                (Integer) payload.get("amount")
                        );
                        eventType = "buy-robot";
                    } catch (Exception e) {
                        response.put("success", false);
                        response.put("moneyChangedBy", 0);
                        response.put("message", e.getMessage());
//                        kafka Produce
                        this.tradingEventProducer.publishTradingResult(response.toString(), transactionId, "buy-error");
                        continue;
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
                        eventType = "buy-item";
                    } catch (Exception e) {
                        response.put("success", false);
                        response.put("moneyChangedBy", 0);
                        response.put("message", e.getMessage());
//                        kafka Produce
                        this.tradingEventProducer.publishTradingResult(response.toString(), transactionId, "buy-error");
                        continue;
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
                    eventType = "sell-resource";
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("moneyChangedBy", 0);
                    response.put("message", e.getMessage());
//                    kafka Produce
                    this.tradingEventProducer.publishTradingResult(response.toString(), transactionId, "sell-error");
                    continue;
                }
            }

            response.put("success", true);
            response.put("moneyChangedBy", moneyChangedBy);
            response.put("message", "success");
//            Kafka produce
            this.tradingEventProducer.publishTradingResult(response.toString(), transactionId, eventType);
        }

        this.itemService.calculateNewItemPrices();
        this.resourceService.calculateNewResourcePrices();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
