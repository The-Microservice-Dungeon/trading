package com.example.trading.item;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends CrudRepository<Item, Integer> {
    Optional<Item> findByName(String name);
}
