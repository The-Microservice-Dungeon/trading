package com.example.trading.station;

import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlanetService {
    @Autowired
    private PlanetRepository planetRepository;

    /**
     * creates a planet (used for internal testing)
     * @param planetId UUID of new planet
     * @return ID of the created planet
     */
    public UUID createNewPlanet(UUID planetId) {
        Planet planet = new Planet(planetId);
        this.planetRepository.save(planet);
        return planet.getPlanetId();
    }

    /**
     * creates a planet
     * @param planetDto dto from kafka event
     */
    public void createNewPlanet(PlanetDto planetDto) {
        Planet planet = new Planet(planetDto.id);
        this.planetRepository.save(planet);
    }

    /**
     * checks if the given id is a registered station in the trading-service
     * @param planetId UUID of planet to check
     * @return Boolean is planet a station
     */
    public boolean checkIfGivenPlanetIsAStation(UUID planetId) {
        Optional<Planet> planet = this.planetRepository.findById(planetId);
        return planet.isPresent();
    }

    /**
     * returns a needed amount of random planets for the spawning of robots
     * @param amount of wanted planet ids
     * @return array of random planet ids
     */
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
