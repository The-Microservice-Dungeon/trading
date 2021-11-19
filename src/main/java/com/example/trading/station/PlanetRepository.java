package com.example.trading.station;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlanetRepository extends CrudRepository<Planet, UUID> {
    Optional<Planet> findByOriginalPlanetId(UUID originalPlanetId);
}
