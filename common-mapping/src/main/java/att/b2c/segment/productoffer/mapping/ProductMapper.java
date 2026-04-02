package att.b2c.segment.productoffer.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import att.b2c.segment.productoffer.Product;
import att.b2c.segment.productoffer.dto.ProductDto;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "offers", ignore = true)
    ProductDto toDto(Product product);

    Product toModel(ProductDto dto);
}
