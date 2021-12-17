package com.example.trading.core.exceptions;


public class PlanetIsNotAStationException extends RuntimeException {
    public PlanetIsNotAStationException(String givenPlanetId) {
        super("The given planet '" + givenPlanetId + "' is not registered as a station.");
    }
}
