package att.b2c.segment.productoffer.mapping;

import org.mapstruct.Mapper;

import att.b2c.segment.productoffer.Product;
import att.b2c.segment.productoffer.dto.ProductSummaryDto;

@Mapper(componentModel = "spring")
public interface ProductSummaryMapper {

    ProductSummaryDto toDto(Product product);
}
