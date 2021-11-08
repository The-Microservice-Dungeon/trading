package com.example.trading.resource;

import com.example.trading.player.PlayerService;
import com.example.trading.station.StationService;
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
    private StationService stationService;

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
//        if (stationService.checkIfGivenPositionIsOneOfTheStations(x , y))
//            return -2;

        // request to robot for inventory

        // basically no errors
        // get response with all resources and amounts

        int fullPrice = amount * resource.get().getCurrentPrice();

        resource.get().addHistory(amount, currentRound);

        return this.playerService.addMoney(playerId, fullPrice);
    }

    public String getResourcePriceList() {
        Iterable<Resource> resources = this.resourceRepository.findAll();
        StringBuilder list = new StringBuilder();

        for (Resource resource : resources) {
            list.append(resource.getName())
                .append(": ")
                .append(resource.getCurrentPrice())
                .append(";\n");
        }

        return list.toString();
    }
}
