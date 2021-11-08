package com.example.trading.resource;

import com.example.trading.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private PlayerService playerService;

    public int createResource(String name, int price) {
        Resource newResource = new Resource(name, price);
        resourceRepository.save(newResource);
        return newResource.getResourceId();
    }

    // sell complete inventory
    public int sellResource(int playerId, String resourceName, int amount, int currentRound) {
        Optional<Resource> resource = this.resourceRepository.findByName(resourceName.toLowerCase());
        if (resource.isEmpty()) throw new IllegalArgumentException("Resource does not exist");

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
