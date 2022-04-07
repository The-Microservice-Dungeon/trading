package dungeon.trading.resource;

import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ResourceRestController {
    private final ResourceService resourceService;

    public ResourceRestController(
        ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/resources")
    public ResponseEntity<?> getInformationAboutAllResources() {
        JSONArray resources = this.resourceService.getResources();
        return new ResponseEntity<JSONArray>(resources, HttpStatus.OK);
    }

    @GetMapping("/resources/history/price")
    public ResponseEntity<?> getResourcePriceHistory() {
        JSONArray resources = this.resourceService.getResourcePriceHistory();
        return new ResponseEntity<JSONArray>(resources, HttpStatus.OK);
    }

    @GetMapping("/resources/history/sell")
    public ResponseEntity<?> getResourceSellHistory() {
        JSONArray resources = this.resourceService.getResourceSellHistory();
        return new ResponseEntity<JSONArray>(resources, HttpStatus.OK);
    }
}
