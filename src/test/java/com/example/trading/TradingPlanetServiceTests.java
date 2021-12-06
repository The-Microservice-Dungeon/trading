package com.example.trading;

import com.example.trading.station.Planet;
import com.example.trading.station.PlanetRepository;
import com.example.trading.station.PlanetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TradingPlanetServiceTests {
    private final PlanetService planetService;
    private final PlanetRepository planetRepository;

    @Autowired
    public TradingPlanetServiceTests(PlanetService planetService, PlanetRepository planetRepository) {
        this.planetService = planetService;
        this.planetRepository = planetRepository;
    }

    @Test
    @Transactional
    public void planetCreationTest() {
        UUID newPlanetId = this.planetService.createNewPlanet(UUID.randomUUID());
        Optional<Planet> planet = this.planetRepository.findById(newPlanetId);
        assertEquals(newPlanetId, planet.get().getPlanetId());
    }

    @Test
    @Transactional
    public void isPlanetAStationCheckTest() {
        UUID orgStationId = UUID.randomUUID();
        UUID stationPlanet = this.planetService.createNewPlanet(orgStationId);
        assertTrue(this.planetService.checkIfGivenPlanetIsAStation(orgStationId));
    }

    @Test
    @Transactional
    public void isPlanetNotAStationCheckTest() {
        assertFalse(this.planetService.checkIfGivenPlanetIsAStation(UUID.randomUUID()));
    }

    @Test
    @Transactional
    public void getRandomPlanetIdsTest() {
        UUID planet1 = this.planetService.createNewPlanet(UUID.randomUUID());
        UUID planet2 = this.planetService.createNewPlanet(UUID.randomUUID());
        UUID planet3 = this.planetService.createNewPlanet(UUID.randomUUID());
        UUID planet4 = this.planetService.createNewPlanet(UUID.randomUUID());
        UUID planet5 = this.planetService.createNewPlanet(UUID.randomUUID());

        System.out.println(this.planetService.getRandomPlanets(2));
        System.out.println(this.planetService.getRandomPlanets(2));
    }
}
