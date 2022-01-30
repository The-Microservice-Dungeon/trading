package dungeon.trading.event;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DomainEventRepository extends CrudRepository<DomainEvent, UUID> {
}
