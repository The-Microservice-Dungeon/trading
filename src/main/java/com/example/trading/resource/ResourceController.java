package com.example.trading.resource;

import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/resource")
    public ResponseEntity<?> getInformationAboutAllItems() {
        JSONArray resources = this.resourceService.getResources();
        return new ResponseEntity<JSONArray>(resources, HttpStatus.OK);
    }

//    @PostMapping("/resource")
//    public ResponseEntity<?> sellAllResources() {
//        int newAMount = this.resourceService.sellResource()
//    }
}
