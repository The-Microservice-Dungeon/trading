package dungeon.trading.item;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends CrudRepository<Item, UUID> {
    Optional<Item> findByName(String name);
    Iterable<Item> findAllByItemType(ItemType type);
}
