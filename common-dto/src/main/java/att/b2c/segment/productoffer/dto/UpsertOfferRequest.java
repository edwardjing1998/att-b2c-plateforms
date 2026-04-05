package att.b2c.segment.productoffer.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertOfferRequest {

    private UUID offerId;
    private String name;
    private String zip;
}
