package dungeon.trading.station;

import lombok.Getter;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Station {
    @Id
    @Getter
    @Column(columnDefinition = "BINARY(16)")
    private UUID planetId;

    public Station() {}

    public Station(UUID planetId) {
        this.planetId = planetId;
    }
}
