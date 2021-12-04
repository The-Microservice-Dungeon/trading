package com.example.trading.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PlanetService {
    @Autowired
    private PlanetRepository planetRepository;

    public UUID createNewPlanet(UUID planetId, String type) {
        Planet planet = new Planet(planetId, type);
        this.planetRepository.save(planet);
        return planet.getPlanetId();
    }

    public void createNewPlanet(PlanetDto planetDto) {
        Planet planet = new Planet(planetDto.id, planetDto.type);
        this.planetRepository.save(planet);
    }

    public boolean checkIfGivenPlanetIsAStation(UUID planetId) {
        Optional<Planet> planet = planetRepository.findById(planetId);

        if (planet.isEmpty()) throw new IllegalArgumentException("Planet does not exist or given ID is wrong");

        return planet.get().isStationOrSpawn();
    }
}
