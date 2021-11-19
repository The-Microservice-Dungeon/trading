package com.example.trading.resource;

import com.example.trading.player.PlayerService;
import com.example.trading.station.PlanetService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlanetService stationService;

    public UUID createResource(String name, int price) {
        Resource newResource = new Resource(name, price);
        resourceRepository.save(newResource);
        return newResource.getResourceId();
    }

    // sell complete inventory
    public int sellResource(UUID playerId, String resourceName, int amount, int currentRound) {
        Optional<Resource> resource = resourceRepository.findByName(resourceName);
        if (resource.isEmpty()) throw new IllegalArgumentException("Resource does not exist");

        // check position
//        if (stationService.checkIfGivenPlanetIsAStation(planetId))
//            return -2;

        // rest call!!! to robot for inventory

        // basically no errors
        // get response with all resources and amounts

        int fullPrice = amount * resource.get().getCurrentPrice();

        resource.get().addHistory(amount, currentRound);

        return this.playerService.addMoney(playerId, fullPrice);
    }

    public JSONArray getResources() {
        Iterable<Resource> resources = this.resourceRepository.findAll();

        JSONArray resourceArray = new JSONArray();

        for (Resource resource : resources) {
            JSONObject jsonResource = new JSONObject();
            jsonResource.put("id", resource.getName());
            jsonResource.put("price", resource.getCurrentPrice());
            resourceArray.appendElement(jsonResource);
        }

        return resourceArray;
    }
}
