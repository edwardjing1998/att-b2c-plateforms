package att.b2c.segment.productoffer;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("offer")
public class Offer {

    @Id
    private UUID offerId;
    private String name;
    private UUID productId;
}
