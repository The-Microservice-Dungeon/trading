package com.example.trading.resource;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ResourceRestController {
    @Autowired
    private ResourceService resourceService;

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
