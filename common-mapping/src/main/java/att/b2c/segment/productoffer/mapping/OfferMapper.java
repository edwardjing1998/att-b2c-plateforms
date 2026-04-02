package att.b2c.segment.productoffer.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import att.b2c.segment.productoffer.Offer;
import att.b2c.segment.productoffer.dto.OfferDto;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    @Mapping(target = "products", ignore = true)
    OfferDto toDto(Offer offer);

    @Mapping(target = "productId", ignore = true)
    Offer toModel(OfferDto dto);
}
