package com.example.trading;

import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.ResourceService;
import com.example.trading.station.PlanetService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TradingControllerTests {

    private final ResourceService resourceService;
    private final ItemService itemService;
    private final PlayerService playerService;
    private final PlanetService planetService;

    private final MockMvc mockMvc;

    @Autowired
    public TradingControllerTests(MockMvc mockMvc,
                                  ResourceService resourceService,
                                  ItemService itemService,
                                  PlayerService playerService,
                                  PlanetService planetService) {
        this.mockMvc = mockMvc;
        this.resourceService = resourceService;
        this.itemService = itemService;
        this.playerService = playerService;
        this.planetService = planetService;
    }

    @Test
    @Transactional
    public void getAllResourcesRestTest() throws Exception {
        this.resourceService.createResource("COAL", 2);
        this.resourceService.createResource("IRON", 5);

        MvcResult result = mockMvc
                .perform(get("/resources").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    public void getAllItemsRestTest() throws Exception {
        this.itemService.createItem("PISTOL", "Can shoot", "item", 50);
        this.itemService.createItem("MINI GUN", "Can shoot a lot", "item", 50);

        MvcResult result = mockMvc
                .perform(get("/items").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    public void getSpecificNonExistentItemRestTest() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/items/NONEXISTENT").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    public void getSpecificItemRestTest() throws Exception {
        this.itemService.createItem("PISTOL", "Can shoot", "item", 50);

        MvcResult result = mockMvc
                .perform(get("/items/PISTOL").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    public void getBalancesRestTest() throws Exception {
        UUID playerId = this.playerService.createPlayer(200);

        MvcResult result = mockMvc
                .perform(get("/balances").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    public void postBuyNormalItemRestTest() throws Exception {
        UUID itemId = this.itemService.createItem("PISTOL", "Can shoot", "item", 50);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());
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
        payloadObject.put("itemName", "PISTOL");
        request.put("payload", payloadObject);
        commandArray.appendElement(request);

        MvcResult result = mockMvc
                .perform(post("/commands")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(commandArray.toJSONString()))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(150, this.playerService.getCurrentMoneyAmount(playerId));
    }

    @Test
    @Transactional
    public void postBuyRobotsRestTest() throws Exception {
        UUID itemId = this.itemService.createItem("ROBOT", "Beep Beep Boop", "robot", 100);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());
        UUID playerId = this.playerService.createPlayer(500);
        UUID transactionId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();

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

        assertEquals(300, this.playerService.getCurrentMoneyAmount(playerId));
    }

    @Test
    @Transactional
    public void postSellInventoryRestTest() throws Exception {
        UUID coalId = this.resourceService.createResource("coal", 5);
        UUID ironId = this.resourceService.createResource("iron", 10);
        UUID planetId = this.planetService.createNewPlanet(UUID.randomUUID());
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

        assertEquals(245, this.playerService.getCurrentMoneyAmount(playerId));
    }



    @Test
    @Transactional
    public void patchChangeItemEconomyParametersRestTest() throws Exception {
        UUID itemId = this.itemService.createItem("PISTOL", "Can shoot", "item", 50);

        JSONObject request = new JSONObject();
        request.put("roundCount", 10);
        request.put("stock", 20);

        MvcResult result = mockMvc
                .perform(patch("/items/PISTOL/economy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.toJSONString()))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

}
