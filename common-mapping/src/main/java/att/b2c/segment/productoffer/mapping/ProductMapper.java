package att.b2c.segment.productoffer.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import att.b2c.segment.productoffer.Product;
import att.b2c.segment.productoffer.dto.ProductResponse;
import att.b2c.segment.productoffer.dto.UpsertProductRequest;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "offers", ignore = true)
    ProductResponse toResponse(Product product);

    Product toModel(UpsertProductRequest request);
}
