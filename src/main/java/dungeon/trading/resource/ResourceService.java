package dungeon.trading.resource;

import dungeon.trading.core.RestService;
import dungeon.trading.core.exceptions.PlanetIsNotAStationException;
import dungeon.trading.core.exceptions.RequestReturnedErrorException;
import dungeon.trading.player.PlayerService;
import dungeon.trading.game.GameService;
import dungeon.trading.station.StationService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
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

    @Value("${dungeon.services.robot}")
    private String robotService;

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
    public Map<String, ?> sellResources(UUID transactionId, UUID playerId, UUID robotId, UUID planetId) {
        if (!this.planetService.checkIfGivenPlanetIsAStation(planetId))
            throw new PlanetIsNotAStationException(planetId.toString());

        ResponseEntity<?> sellResponse = this.restService.post(
                this.robotService + "/robots/" + robotId + "/inventory/clearResources",
                null,
                JSONObject.class
        );

        if (sellResponse.getStatusCode() != HttpStatus.OK)
            throw new RequestReturnedErrorException(sellResponse.getBody().toString());

        JSONObject responseBody = (JSONObject) sellResponse.getBody();

        Map<String, Object> returnData = new HashMap<>();
        if (responseBody == null) {
            returnData.put("moneyChangedBy", 0);
            returnData.put("message", "Robot inventory is empty");
            returnData.put("data", null);
            return returnData;
        }

        int fullAmount = 0;

        for (String key : responseBody.keySet()) {
            Optional<Resource> resource = this.resourceRepository.findByName(key);
            if (resource.isEmpty()) continue;

            fullAmount += (Integer) responseBody.get(key) * resource.get().getCurrentPrice();
            resource.get().addHistory(this.gameService.getRoundCount(), (Integer) responseBody.get(key));
        }

        this.playerService.addMoney(playerId, fullAmount);

        returnData.put("moneyChangedBy", fullAmount);
        returnData.put("message", "resources sold");
        returnData.put("data", responseBody);
        return returnData;
    }

    /**
     * returns all resources with current prices
     * used for the events and rest-calls
     * @return JSONArray
     */
    public JSONArray getResources() {
        JSONArray resourceArray = new JSONArray();

        for (Resource resource : this.resourceRepository.findAll()) {
            JSONObject jsonResource = new JSONObject();
            jsonResource.put("name", resource.getName());
            jsonResource.put("price", resource.getCurrentPrice());
            resourceArray.appendElement(jsonResource);
        }

        return resourceArray;
    }

    /**
     * returns all resources with their complete price history
     * used for the according rest-call /resources/history/price
     * @return JSONArray
     */
    public JSONArray getResourcePriceHistory() {
        JSONArray resourceArray = new JSONArray();

        for (Resource resource : this.resourceRepository.findAll()) {
            JSONObject jsonResource = new JSONObject();
            jsonResource.put("name", resource.getName());
            jsonResource.put("history", resource.getPriceHistory());
            resourceArray.appendElement(jsonResource);
        }

        return resourceArray;
    }

    /**
     * returns all resources with their complete sell history
     * used for the according rest-call /resources/history/sell
     * @return JSONArray
     */
    public JSONArray getResourceSellHistory() {
        JSONArray resourceArray = new JSONArray();

        for (Resource resource : this.resourceRepository.findAll()) {
            JSONObject jsonResource = new JSONObject();
            jsonResource.put("name", resource.getName());
            jsonResource.put("history", resource.getSellHistory());
            resourceArray.appendElement(jsonResource);
        }

        return resourceArray;
    }

    /**
     * calculates the new resource prices and emits them as an event
     */
    public void calculateNewResourcePrices() {
        JSONArray resources = new JSONArray();

        for (Resource resource : this.resourceRepository.findAll()) {
            resource.calculateNewPrice(this.gameService.getRoundCount());

            JSONObject jsonResource = new JSONObject();
            jsonResource.put("name", resource.getName());
            jsonResource.put("price", resource.getCurrentPrice());
            resources.add(jsonResource);
        }

        this.resourceEventProducer.publishNewResourcePrices(resources);
    }

    public void resetResources() {
        this.removeResources();
        this.createResources();
    }

    /**
     * creates all resources on startup
     */
    @PostConstruct
    public void createResources() {
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
            log.error("Could not find resource file", e);
        }
    }

    @PreDestroy
    public void removeResources() {
        this.resourceRepository.deleteAll();
    }
}
