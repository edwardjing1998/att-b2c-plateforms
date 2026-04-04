package att.b2c.segment.customer.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableOffersEventDto {

    private UUID eventId;
    private Instant createdAt;
    private CustomerOffersDto payload;
}
