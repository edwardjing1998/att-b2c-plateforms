package att.b2c.segment.productoffer;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("product")
public class Product {

    @Id
    private UUID productId;

    private String name;

    private String description;

    private BigDecimal price;
}
