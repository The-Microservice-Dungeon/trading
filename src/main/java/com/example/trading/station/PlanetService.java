package com.example.trading.station;

import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlanetService {
    @Autowired
    private PlanetRepository planetRepository;

    public UUID createNewPlanet(UUID planetId) {
        Planet planet = new Planet(planetId);
        this.planetRepository.save(planet);
        return planet.getPlanetId();
    }

    public void createNewPlanet(PlanetDto planetDto) {
        Planet planet = new Planet(planetDto.id);
        this.planetRepository.save(planet);
    }

    public boolean checkIfGivenPlanetIsAStation(UUID planetId) {
        Optional<Planet> planet = this.planetRepository.findById(planetId);
        return planet.isPresent();
    }

    public JSONArray getRandomPlanets(int amount) {
        Iterable<Planet> temp = this.planetRepository.findAll();
        List<Planet> planets = new ArrayList<>();
        temp.forEach(planets::add);
        JSONArray planetArray = new JSONArray();

        Random random = new Random();
        for (int i = 0; i < amount; i++) {
            planetArray.add(
                    planets.get(
                            random.nextInt(planets.size())
                    ).getPlanetId().toString()
            );
        }

        return planetArray;
    }
}
