package dungeon.trading;

import dungeon.trading.station.Station;
import dungeon.trading.station.StationRepository;
import dungeon.trading.station.StationService;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TradingPlanetServiceTests {
    private final StationService stationService;
    private final StationRepository stationRepository;

    @Autowired
    public TradingPlanetServiceTests(StationService stationService, StationRepository stationRepository) {
        this.stationService = stationService;
        this.stationRepository = stationRepository;
    }

    @Test
    @Transactional
    public void planetCreationTest() {
        UUID newPlanetId = this.stationService.createNewStation(UUID.randomUUID());
        Optional<Station> planet = this.stationRepository.findById(newPlanetId);
        assertEquals(newPlanetId, planet.get().getPlanetId());
    }

    @Test
    @Transactional
    public void isPlanetAStationCheckTest() {
        UUID stationId = this.stationService.createNewStation(UUID.randomUUID());
        assertTrue(this.stationService.checkIfGivenPlanetIsAStation(stationId));
    }

    @Test
    @Transactional
    public void isPlanetNotAStationCheckTest() {
        assertFalse(this.stationService.checkIfGivenPlanetIsAStation(UUID.randomUUID()));
    }

    @Test
    @Transactional
    public void getRandomPlanetIdsTest() {
        ArrayList<String> planets = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            planets.add(this.stationService.createNewStation(UUID.randomUUID()).toString());
        }

        JSONArray planetArray = this.stationService.getRandomStations(6);

        boolean arrayIncludes = true;
        for (int i = 0; i < 6; i++) {
            if (!planets.contains((String) planetArray.get(i))) {
                arrayIncludes = false;
                break;
            }
        }

        assertTrue(arrayIncludes);
    }
}
