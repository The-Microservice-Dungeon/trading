package dungeon.trading.event;

import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class DomainEventService {
    @Autowired
    private DomainEventRepository domainEventRepository;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public String saveDomainEvent(String payload, Headers headers) {
        DomainEvent event = new DomainEvent(payload, headers);
        this.domainEventRepository.save(event);
        return event.getTransactionId();
    }

    public DomainEvent createDomainEvent(String payload, String transactionId, String version, String type) {
        DomainEvent event = new DomainEvent(
                payload,
                UUID.randomUUID().toString(),
                transactionId,
                version,
                this.sdf.format(new Date()),
                type
        );

        this.domainEventRepository.save(event);
        return event;
    }

    @PreDestroy
    public void removeAllOldDomainEvents() {
        this.domainEventRepository.deleteAll();
    }
}
