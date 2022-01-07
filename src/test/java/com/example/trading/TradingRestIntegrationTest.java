package com.example.trading;

import com.example.trading.player.PlayerService;
import com.example.trading.station.StationService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TradingRestIntegrationTest {

    private final StationService stationService;
    private final PlayerService playerService;
    private final MockMvc mockMvc;

    @Autowired
    public TradingRestIntegrationTest(MockMvc mockMvc,
                                      PlayerService playerService,
                                      StationService stationService) {
        this.mockMvc = mockMvc;
        this.playerService = playerService;
        this.stationService = stationService;
    }

    @Test
    @Transactional
    public void postSellInventoryRestTest() throws Exception {
        UUID planetId = this.stationService.createNewStation(UUID.randomUUID());
        UUID playerId = this.playerService.createPlayer(200);
        UUID transactionId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();

        JSONArray commandArray = new JSONArray();
        JSONObject request = new JSONObject();
        request.put("transactionId", transactionId.toString());
        request.put("playerId", playerId.toString());
        JSONObject payloadObject = new JSONObject();
        payloadObject.put("commandType", "sell");
        payloadObject.put("robotId", robotId.toString());
        payloadObject.put("planetId", planetId.toString());
        request.put("payload", payloadObject);
        commandArray.appendElement(request);

        MvcResult result = mockMvc
                .perform(post("/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commandArray.toJSONString()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(content);

        assertEquals("true", response.get("success"));
        assertTrue(this.playerService.getCurrentMoneyAmount(playerId) >= 200);
    }

    @Test
    @Transactional
    public void postBuyNormalItemRestTest() throws Exception {
        UUID planetId = this.stationService.createNewStation(UUID.randomUUID());
        UUID playerId = this.playerService.createPlayer(200);
        UUID transactionId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();

        JSONArray commandArray = new JSONArray();
        JSONObject request = new JSONObject();
        request.put("transactionId", transactionId.toString());
        request.put("playerId", playerId.toString());
        JSONObject payloadObject = new JSONObject();
        payloadObject.put("commandType", "buy");
        payloadObject.put("robotId", robotId.toString());
        payloadObject.put("planetId", planetId.toString());
        payloadObject.put("itemName", "ROCKET");
        request.put("payload", payloadObject);
        commandArray.appendElement(request);

        MvcResult result = mockMvc
                .perform(post("/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commandArray.toJSONString()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(content);

        assertEquals("true", response.get("success"));
        assertEquals(160, this.playerService.getCurrentMoneyAmount(playerId));
    }

    @Test
    @Transactional
    public void postBuyRobotsRestTest() throws Exception {
        UUID planetId = this.stationService.createNewStation(UUID.randomUUID());
        UUID playerId = this.playerService.createPlayer(500);
        UUID transactionId = UUID.randomUUID();

        JSONArray commandArray = new JSONArray();
        JSONObject request = new JSONObject();
        request.put("transactionId", transactionId.toString());
        request.put("playerId", playerId.toString());
        JSONObject payloadObject = new JSONObject();
        payloadObject.put("commandType", "buy");
        payloadObject.put("itemName", "ROBOT");
        payloadObject.put("amount", 2);
        request.put("payload", payloadObject);
        commandArray.appendElement(request);

        MvcResult result = mockMvc
                .perform(post("/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commandArray.toJSONString()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(content);

        assertEquals("true", response.get("success"));
        assertEquals(300, this.playerService.getCurrentMoneyAmount(playerId));
    }
}
