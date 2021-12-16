package com.example.trading.resource;

import com.example.trading.RestService;
import com.example.trading.core.exceptions.PlanetIsNotAStationException;
import com.example.trading.core.exceptions.RequestReturnedErrorException;
import com.example.trading.core.exceptions.ResourceDoesNotExistException;
import com.example.trading.item.Item;
import com.example.trading.player.PlayerService;
import com.example.trading.round.RoundService;
import com.example.trading.station.PlanetService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileReader;
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

    @Autowired
    private RoundService roundService;

    @Autowired
    private RestService restService;

    private ResourceEventProducer resourceEventProducer;

    public UUID createResource(String name, int price) {
        Optional<Resource> resource = this.resourceRepository.findByName(name);
        if (resource.isPresent()) return resource.get().getResourceId();

        Resource newResource = new Resource(name, price);
        this.resourceRepository.save(newResource);
        return newResource.getResourceId();
    }

    public int sellResources(UUID transactionId, UUID playerId, UUID robotId, UUID planetId) {
        if (!this.planetService.checkIfGivenPlanetIsAStation(planetId))
            throw new PlanetIsNotAStationException(planetId.toString());

        ResponseEntity<?> sellResponse = null;
//        sellResponse = this.restService.post(System.getenv("ROBOT_SERVICE") + "/robots/" + robotId + "/inventory/clearResources", null, JSONObject.class);

        // mock data
        JSONObject robotInventory = new JSONObject();
        robotInventory.put("coal", 5);
        robotInventory.put("iron", 2);
        sellResponse = new ResponseEntity<>(robotInventory, HttpStatus.OK);

        if (sellResponse.getStatusCode() != HttpStatus.OK)
            throw new RequestReturnedErrorException(sellResponse.getBody().toString());

        JSONObject responseBody = (JSONObject) sellResponse.getBody();

        if (responseBody == null) return this.playerService.getCurrentMoneyAmount(playerId);

        int fullAmount = 0;

        for (String key : responseBody.keySet()) {
            Optional<Resource> resource = this.resourceRepository.findByName(key);
            if (resource.isEmpty()) continue;

            fullAmount += (Integer) responseBody.get(key) * resource.get().getCurrentPrice();
            resource.get().addHistory(this.roundService.getRoundCount(), (Integer) responseBody.get(key));
        }

        this.playerService.addMoney(playerId, fullAmount);
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

    public void patchItemEconomyParameters(String name, JSONObject parameters) throws Exception {
        Optional<Resource> resource = this.resourceRepository.findByName(name);
        if (resource.isEmpty()) throw new ResourceDoesNotExistException(name);

        try {
            resource.get().changeEconomyParameters(
                    (Integer) parameters.get("roundCount"),
                    (Integer) parameters.get("demand")
            );
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void calculateNewResourcePrices() {
        Iterable<Resource> resources = this.resourceRepository.findAll();
        for (Resource resource : resources) {
            resource.calculateNewPrice(this.roundService.getRoundCount());
        }

//        this.resourceEventProducer.publishNewResourcePrices(this.resourceRepository.findAll().toString());
    }

    @PostConstruct
    public void createResourcesOnStartup() {
        JSONParser parser = new JSONParser();
        try {
            JSONArray resourceArray = (JSONArray) parser.parse(new FileReader("src/main/resources/resources.json"));

            for (Object resource : resourceArray) {
                JSONObject jsonResource = (JSONObject) resource;
                this.createResource(
                    jsonResource.get("name").toString(),
                    (int) jsonResource.get("price")
                );
            }
        } catch (Exception e) {
            System.out.println("Could not find File");
        }
    }

    @PreDestroy
    public void removeResourcesOnStop() {
        this.resourceRepository.deleteAll();
    }
}
