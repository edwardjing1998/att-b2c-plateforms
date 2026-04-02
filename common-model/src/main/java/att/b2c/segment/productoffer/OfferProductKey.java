package att.b2c.segment.productoffer;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyClass
public class OfferProductKey implements Serializable {

    @PrimaryKeyColumn(name = "offer_id", type = PrimaryKeyType.PARTITIONED)
    private UUID offerId;

    @PrimaryKeyColumn(name = "product_id", type = PrimaryKeyType.CLUSTERED)
    private UUID productId;
}
