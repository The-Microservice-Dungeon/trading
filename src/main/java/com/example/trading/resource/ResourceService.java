package com.example.trading.resource;

import com.example.trading.core.RestService;
import com.example.trading.core.exceptions.PlanetIsNotAStationException;
import com.example.trading.core.exceptions.RequestReturnedErrorException;
import com.example.trading.player.PlayerService;
import com.example.trading.game.GameService;
import com.example.trading.station.StationService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private StationService planetService;

    @Autowired
    private GameService gameService;

    @Autowired
    private RestService restService;

    @Autowired
    private ResourceEventProducer resourceEventProducer;

    /**
     * creates resource or returns the id if it already exists
     * @param name of the resource
     * @param price of the resource
     * @return UUID of the created resource
     */
    public UUID createResource(String name, int price) {
        Optional<Resource> resource = this.resourceRepository.findByName(name);
        if (resource.isPresent()) return resource.get().getResourceId();

        Resource newResource = new Resource(name, price);
        this.resourceRepository.save(newResource);
        return newResource.getResourceId();
    }

    /**
     * command handler for selling resources
     * does a rest call to robot-service to get the inventory of a robot
     * calculates the price afterwards
     * @param transactionId from the command
     * @param playerId from the command
     * @param robotId from the command
     * @param planetId from the command
     * @return amount of money gotten from the selling of the resources
     */
    public int sellResources(UUID transactionId, UUID playerId, UUID robotId, UUID planetId) {
        if (!this.planetService.checkIfGivenPlanetIsAStation(planetId))
            throw new PlanetIsNotAStationException(planetId.toString());

        ResponseEntity<?> sellResponse = null;
        sellResponse = this.restService.post(System.getenv("ROBOT_SERVICE") + "/robots/" + robotId + "/inventory/clearResources", null, JSONObject.class);

        if (sellResponse.getStatusCode() != HttpStatus.OK)
            throw new RequestReturnedErrorException(sellResponse.getBody().toString());

        JSONObject responseBody = (JSONObject) sellResponse.getBody();

        if (responseBody == null) return this.playerService.getCurrentMoneyAmount(playerId);

        int fullAmount = 0;

        for (String key : responseBody.keySet()) {
            Optional<Resource> resource = this.resourceRepository.findByName(key);
            if (resource.isEmpty()) continue;

            fullAmount += (Integer) responseBody.get(key) * resource.get().getCurrentPrice();
            resource.get().addHistory(this.gameService.getRoundCount(), (Integer) responseBody.get(key));
        }

        this.playerService.addMoney(playerId, fullAmount);
        return fullAmount;
    }

    /**
     * returns all resources with current prices
     * used for the events and rest-calls
     * @return array with resources
     */
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

    /**
     * calculates the new resource prices and emits them as an event
     */
    public void calculateNewResourcePrices() {
        Iterable<Resource> resources = this.resourceRepository.findAll();
        for (Resource resource : resources) {
            resource.calculateNewPrice(this.gameService.getRoundCount());
        }

        this.resourceEventProducer.publishNewResourcePrices(this.resourceRepository.findAll().toString());
    }

    /**
     * creates all resources on startup
     */
    @PostConstruct
    public void createResourcesOnStartup() {
        JSONParser parser = new JSONParser();
        try {
            File file = ResourceUtils.getFile("classpath:resources.json");
            InputStream in = new FileInputStream(file);
            JSONArray resourceArray = (JSONArray) parser.parse(new InputStreamReader(in, StandardCharsets.UTF_8));

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
