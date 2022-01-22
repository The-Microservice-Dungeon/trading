package dungeon.trading;

import dungeon.trading.item.ItemService;
import dungeon.trading.player.PlayerService;
import dungeon.trading.resource.ResourceService;
import dungeon.trading.game.GameService;
import dungeon.trading.station.StationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TradingRestControllerTests {

    private final ResourceService resourceService;
    private final ItemService itemService;
    private final PlayerService playerService;
    private final StationService planetService;
    private final GameService gameService;

    private final MockMvc mockMvc;

    @Autowired
    public TradingRestControllerTests(MockMvc mockMvc,
                                  ResourceService resourceService,
                                  ItemService itemService,
                                  PlayerService playerService,
                                  StationService planetService,
                                  GameService gameService) {
        this.mockMvc = mockMvc;
        this.resourceService = resourceService;
        this.itemService = itemService;
        this.playerService = playerService;
        this.planetService = planetService;
        this.gameService = gameService;
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
    public void getItemsPriceHistoryRestTest() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/items/history/price").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertNotEquals(
                "[]",
                result.getResponse().getContentAsString()
        );
    }

    @Test
    @Transactional
    public void getItemsBuyHistoryRestTest() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/items/history/buy").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertNotEquals(
                "[]",
                result.getResponse().getContentAsString()
        );
    }

    @Test
    @Transactional
    public void getResourcesPriceHistoryRestTest() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/resources/history/price").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertNotEquals(
                "[]",
                result.getResponse().getContentAsString()
        );
    }

    @Test
    @Transactional
    public void getResourcesSellHistoryRestTest() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/resources/history/sell").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertNotEquals(
                "[]",
                result.getResponse().getContentAsString()
        );
    }


}
