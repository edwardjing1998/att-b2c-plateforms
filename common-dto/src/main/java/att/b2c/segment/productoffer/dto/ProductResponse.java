package att.b2c.segment.productoffer.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;

    private List<OfferSummaryDto> offers;
}
