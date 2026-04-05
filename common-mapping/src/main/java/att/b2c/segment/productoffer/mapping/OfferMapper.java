package att.b2c.segment.productoffer.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import att.b2c.segment.productoffer.Offer;
import att.b2c.segment.productoffer.dto.OfferResponse;
import att.b2c.segment.productoffer.dto.UpsertOfferRequest;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    @Mapping(target = "products", ignore = true)
    OfferResponse toResponse(Offer offer);

    @Mapping(target = "productId", ignore = true)
    Offer toModel(UpsertOfferRequest request);
}
