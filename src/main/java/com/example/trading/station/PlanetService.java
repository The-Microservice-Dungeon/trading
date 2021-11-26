package com.example.trading.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PlanetService {
    @Autowired
    private PlanetRepository planetRepository;

    public UUID createNewPlanet(UUID orgPlanetId, String type) {
        Planet planet = new Planet(orgPlanetId, type);
        this.planetRepository.save(planet);
        return planet.getOriginalPlanetId();
    }

    public boolean checkIfGivenPlanetIsAStation(UUID planetId) {
        Optional<Planet> planet = planetRepository.findByOriginalPlanetId(planetId);

        if (planet.isEmpty()) throw new IllegalArgumentException("Planet does not exist or given ID is wrong");

        return planet.get().isStationOrSpawn();
    }
}
