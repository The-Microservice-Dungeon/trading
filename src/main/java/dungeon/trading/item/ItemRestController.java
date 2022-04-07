package dungeon.trading.item;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class ItemRestController {
    private final ItemService itemService;

    public ItemRestController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/items")
    public ResponseEntity<?> getInformationAboutAllItems() {
        JSONArray items = this.itemService.getItems();
        return new ResponseEntity<JSONArray>(items, HttpStatus.OK);
    }

    @GetMapping("/items/{item-name}")
    public ResponseEntity<?> getInformationAboutOneItem(@PathVariable("item-name") String itemId) {
        try {
            JSONObject foundItem = this.itemService.getItem(itemId);
            return new ResponseEntity<JSONObject>(foundItem, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Could not retrieve Item {}", itemId, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/items/history/price")
    public ResponseEntity<?> getItemPriceHistory() {
        JSONArray items = this.itemService.getItemPriceHistory();
        return new ResponseEntity<JSONArray>(items, HttpStatus.OK);
    }

    @GetMapping("/items/history/buy")
    public ResponseEntity<?> getItemBuyHistory() {
        JSONArray items = this.itemService.getItemBuyHistory();
        return new ResponseEntity<JSONArray>(items, HttpStatus.OK);
    }
}
