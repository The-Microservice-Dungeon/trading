package dungeon.trading.resource;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResourceRepository extends CrudRepository<Resource, UUID> {
    Optional<Resource> findByName(String name);
}
