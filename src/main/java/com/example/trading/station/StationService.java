package com.example.trading.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationService {
    @Autowired
    private StationRepository stationRepository;

    public boolean checkIfGivenPositionIsOneOfTheStations(int x, int y) {
        Iterable<Station> stations = stationRepository.findAll();
        for (Station station : stations) {
            if (station.checkPosition(x, y)) {
                return true;
            }
        }

        return false;
    }
}
