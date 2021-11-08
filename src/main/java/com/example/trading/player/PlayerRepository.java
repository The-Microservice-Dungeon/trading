package com.example.trading.player;

import com.example.trading.item.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

//public interface PlayerRepository extends CrudRepository<Player, Integer> {
public interface PlayerRepository extends CrudRepository<Player, UUID> {
}
