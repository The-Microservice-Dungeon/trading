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

    @PatchMapping("/resources/{resource-name}/economy")
    public ResponseEntity<?> patchResourceEconomyParameters(@PathVariable("resource-name") String resourceName, @RequestBody String newParameters) {
        JSONParser parser = new JSONParser();
        JSONObject parameters = new JSONObject();
        try {
            parameters = (JSONObject) parser.parse(newParameters);
        } catch (Exception e) {
            System.out.println("Cant Parse String: " + e.getMessage());
        }

        try {
            this.resourceService.patchItemEconomyParameters(resourceName, parameters);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
