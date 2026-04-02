package att.b2c.segment.customer.dto;

import java.util.List;

import att.b2c.segment.productoffer.dto.OfferDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOffersDto {

    private CustomerDto customer;
    private List<OfferDto> offers;
}
