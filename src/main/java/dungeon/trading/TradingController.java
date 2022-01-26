package dungeon.trading;

import dungeon.trading.item.ItemService;
import dungeon.trading.player.PlayerService;
import dungeon.trading.resource.ResourceService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
public class TradingController {
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ItemService itemService;

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

            Map<String, ?> result = null;

            String transactionId = command.get("transactionId").toString();
            String eventType = "";

            if (Objects.equals(payload.get("commandType"), "buy")) {
                String item = null;

                try {
                    item = (String) payload.get("itemName");
                } catch (Exception e) {
                    this.sendErrorEvent(e.getMessage(), transactionId, "buy-error");
                    continue;
                }

                if (Objects.equals(item, "ROBOT")) {
                    try {
                        result = this.itemService.buyRobots(
                                UUID.fromString((String) command.get("transactionId")),
                                UUID.fromString((String) command.get("playerId")),
                                (Integer) payload.get("amount")
                        );
                        eventType = "buy-robot";
                    } catch (Exception e) {
                        this.sendErrorEvent(e.getMessage(), transactionId, "buy-error");
                        continue;
                    }

                } else if (item != null) {
                    try {
                        result = this.itemService.buyItem(
                                UUID.fromString((String) command.get("transactionId")),
                                UUID.fromString((String) command.get("playerId")),
                                UUID.fromString((String) payload.get("robotId")),
                                UUID.fromString((String) payload.get("planetId")),
                                (String) payload.get("itemName")
                        );
                        eventType = "buy-item";
                    } catch (Exception e) {
                        this.sendErrorEvent(e.getMessage(), transactionId, "buy-error");
                        continue;
                    }

                } else {
                    System.out.println("itemName not given");
                }

            } else if (Objects.equals(payload.get("commandType"), "sell")) {
                try {
                    result = this.resourceService.sellResources(
                            UUID.fromString((String) command.get("transactionId")),
                            UUID.fromString((String) command.get("playerId")),
                            UUID.fromString((String) payload.get("robotId")),
                            UUID.fromString((String) payload.get("planetId"))
                    );
                    eventType = "sell-resource";
                } catch (Exception e) {
                    this.sendErrorEvent(e.getMessage(), transactionId, "sell-error");
                    continue;
                }
            }

            response.put("success", true);
            response.put("moneyChangedBy", result.get("moneyChangedBy"));
            response.put("message", result.get("message"));
            response.put("data", result.get("data"));
//            Kafka produce
            this.tradingEventProducer.publishTradingResult(response.toString(), transactionId, eventType);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void sendErrorEvent(String exceptionMessage, String transactionId, String type) {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("moneyChangedBy", 0);
        response.put("message", exceptionMessage);
        response.put("data", null);
        this.tradingEventProducer.publishTradingResult(response.toString(), transactionId, type);
    }
}
