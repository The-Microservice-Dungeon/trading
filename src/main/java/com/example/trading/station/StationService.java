package com.example.trading.station;

import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StationService {
    @Autowired
    private StationRepository stationRepository;

    /**
     * creates a planet (used for internal testing)
     * @param planetId UUID of new planet
     * @return ID of the created station
     */
    public UUID createNewStation(UUID planetId) {
        Station planet = new Station(planetId);
        this.stationRepository.save(planet);
        return planet.getPlanetId();
    }

    /**
     * creates a planet
     * @param stationDto dto from kafka event
     */
    public void createNewStation(StationDto stationDto) {
        Station station = new Station(stationDto.id);
        this.stationRepository.save(station);
    }

    /**
     * checks if the given id is a registered station in the trading-service
     * @param planetId UUID of planet to check
     * @return Boolean is planet a station
     */
    public boolean checkIfGivenPlanetIsAStation(UUID planetId) {
        Optional<Station> station = this.stationRepository.findById(planetId);
        return station.isPresent();
    }

    /**
     * returns a needed amount of random planets for the spawning of robots
     * @param amount of wanted planet ids
     * @return array of random planet ids
     */
    public JSONArray getRandomStations(int amount) {
        Iterable<Station> temp = this.stationRepository.findAll();
        List<Station> stations = new ArrayList<>();
        temp.forEach(stations::add);
        JSONArray stationArray = new JSONArray();

        Random random = new Random();
        for (int i = 0; i < amount; i++) {
            stationArray.add(
                    stations.get(
                            random.nextInt(stations.size())
                    ).getPlanetId().toString()
            );
        }

        return stationArray;
    }
}
