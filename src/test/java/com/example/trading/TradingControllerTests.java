package com.example.trading;

import com.example.trading.item.ItemService;
import com.example.trading.resource.ResourceService;
import net.minidev.json.JSONArray;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TradingControllerTests {

    private final ResourceService resourceService;
    private final ItemService itemService;

    private final MockMvc mockMvc;

    @Autowired
    public TradingControllerTests(MockMvc mockMvc,
                                  ResourceService resourceService,
                                  ItemService itemService) {
        this.mockMvc = mockMvc;
        this.resourceService = resourceService;
        this.itemService = itemService;
    }

    @Test
    @Transactional
    public void getAllResourcesRestTest() throws Exception {
        this.resourceService.createResource("COAL", 2);
        this.resourceService.createResource("IRON", 5);

        MvcResult result = mockMvc
                .perform(
                        get("/resources")
                                .contentType(MediaType.APPLICATION_JSON))
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
                .perform(
                        get("/items")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    public void getSpecificNonExistentItemRestTest() throws Exception {
        MvcResult result = mockMvc
                .perform(
                        get("/items/NONEXISTENT")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    public void getSpecificItemRestTest() throws Exception {
        this.itemService.createItem("PISTOL", "Can shoot", "item", 50);

        MvcResult result = mockMvc
                .perform(
                        get("/items/PISTOL")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

}
