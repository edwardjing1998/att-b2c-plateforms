package att.b2c.segment.productoffer.dto;

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
public class OfferResponse {

    private UUID offerId;
    private String name;
    private String zip;

    private List<ProductSummaryDto> products;
}
