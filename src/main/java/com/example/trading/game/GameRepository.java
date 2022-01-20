package com.example.trading.game;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends CrudRepository<Game, UUID> {
    Optional<Game> findByIsCurrentGame(Boolean bool);
}
