package com.example.trading;

import com.example.trading.item.ItemService;
import com.example.trading.player.PlayerService;
import com.example.trading.resource.ResourceService;
import com.example.trading.round.RoundDto;
import com.example.trading.round.RoundService;
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
    private final RoundService roundService;

    private final MockMvc mockMvc;

    @Autowired
    public TradingControllerTests(MockMvc mockMvc,
                                  ResourceService resourceService,
                                  ItemService itemService,
                                  PlayerService playerService,
                                  PlanetService planetService,
                                  RoundService roundService) {
        this.mockMvc = mockMvc;
        this.resourceService = resourceService;
        this.itemService = itemService;
        this.playerService = playerService;
        this.planetService = planetService;
        this.roundService = roundService;
    }

    @Test
    @Transactional
    public void getAllResourcesRestTest() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/resources").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertNotEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    public void getAllItemsRestTest() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/items").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertNotEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    public void getSpecificNonExistentItemRestTest() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/items/NONEXISTENT").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertEquals(
                "The item/upgrade 'NONEXISTENT' does not exist.",
                result.getResponse().getContentAsString()
        );
    }

    @Test
    @Transactional
    public void getSpecificItemRestTest() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/items/ROCKET").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(
                "{\"price\":40,\"item-name\":\"ROCKET\",\"type\":\"item\"}",
                result.getResponse().getContentAsString()
        );
    }

    @Test
    @Transactional
    public void getBalancesRestTest() throws Exception {
        UUID playerId = this.playerService.createPlayer(200);

        MvcResult result = mockMvc
                .perform(get("/balances").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(
                "[{\"balance\":200,\"player-id\":\"" + playerId + "\"}]",
                result.getResponse().getContentAsString()
        );
    }

    @Test
    @Transactional
    public void getBalancesForSpecificRoundRestTest() throws Exception {
        this.roundService.updateRound(new RoundDto(1, "started"));
        UUID playerId = this.playerService.createPlayer(200);
        this.roundService.updateRound(new RoundDto(1, "ended"));

        MvcResult result = mockMvc
                .perform(get("/balances/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(
                "[{\"round\":1,\"balance\":200,\"player-id\":\"" + playerId + "\"}]",
                result.getResponse().getContentAsString()
        );
    }


    @Test
    @Transactional
    public void patchChangeItemEconomyParametersRestTest() throws Exception {
        JSONObject request = new JSONObject();
        request.put("roundCount", 10);
        request.put("stock", 20);

        MvcResult result = mockMvc
                .perform(patch("/items/ROCKET/economy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.toJSONString()))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }
}
