package com.example.trading.resource;

import com.example.trading.item.Item;
import com.example.trading.player.PlayerService;
import com.example.trading.station.PlanetService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlanetService planetService;

    public UUID createResource(String name, int price) {
        Resource resource = new Resource(name, price);
        this.resourceRepository.save(resource);
        return resource.getResourceId();
    }

    public void createResource(ResourceDto resourceDto) {
        Resource resource = new Resource(resourceDto.name, resourceDto.price);
        this.resourceRepository.save(resource);
    }

    // sell complete inventory
    public int sellResources(UUID transactionId, UUID playerId, UUID robotId, UUID planetId, int currentRound) {
        if (!this.planetService.checkIfGivenPlanetIsAStation(planetId)) return -2;

        // post to /robots/{robot-uuid}/inventory/clearResources
        ResponseEntity<?> sellResponse;
        // rest call!!! to robot for inventory

        // mock data
        JSONObject robotInventory = new JSONObject();
        robotInventory.put("coal", 5);
        robotInventory.put("iron", 2);
        sellResponse = new ResponseEntity<>(robotInventory, HttpStatus.OK);
//        sellResponse = new ResponseEntity<>("Request could not be accepted", HttpStatus.BAD_REQUEST);
//        sellResponse = new ResponseEntity<>("Robot not found", HttpStatus.NOT_FOUND);

        if (sellResponse.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException(sellResponse.getBody().toString());
        }

        JSONObject responseBody = (JSONObject) sellResponse.getBody();

        if (responseBody == null) return this.playerService.getCurrentMoneyAmount(playerId);

        int fullAmount = 0;

        for (String key : responseBody.keySet()) {
            Optional<Resource> resource = this.resourceRepository.findByName(key);
            if (resource.isEmpty()) continue;

            fullAmount += (Integer) responseBody.get(key) * resource.get().getCurrentPrice();
            resource.get().addHistory(currentRound, (Integer) responseBody.get(key));
        }

        int newAmount = this.playerService.addMoney(playerId, fullAmount);
        return fullAmount;
    }

    public JSONArray getResources() {
        Iterable<Resource> resources = this.resourceRepository.findAll();

        JSONArray resourceArray = new JSONArray();

        for (Resource resource : resources) {
            JSONObject jsonResource = new JSONObject();
            jsonResource.put("name", resource.getName());
            jsonResource.put("price", resource.getCurrentPrice());
            resourceArray.appendElement(jsonResource);
        }

        return resourceArray;
    }
}
