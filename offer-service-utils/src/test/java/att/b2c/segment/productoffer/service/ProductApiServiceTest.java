package att.b2c.segment.productoffer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import att.b2c.segment.productoffer.Product;
import att.b2c.segment.productoffer.dto.ProductResponse;
import att.b2c.segment.productoffer.error.NotFoundException;
import att.b2c.segment.productoffer.mapping.ProductMapper;

@ExtendWith(MockitoExtension.class)
class ProductApiServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductApiService productApiService;

    @Test
    void getProducts_mapsAll() {
        UUID productId = UUID.randomUUID();
        Product product = new Product(productId, "p1", "d1", BigDecimal.TEN);
        when(productService.findAll()).thenReturn(List.of(product));

        ProductResponse dto = ProductResponse.builder().productId(productId).name("p1").description("d1").price(BigDecimal.TEN).build();
        when(productMapper.toResponse(eq(product))).thenReturn(dto);

        List<ProductResponse> result = productApiService.getProducts();

        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getProductId());
    }

    @Test
    void getProduct_whenMissing_throwsNotFound() {
        UUID productId = UUID.randomUUID();
        when(productService.findById(eq(productId))).thenReturn(null);

        assertThrows(NotFoundException.class, () -> productApiService.getProduct(productId));
    }

    @Test
    void deleteProduct_delegatesToService() {
        UUID productId = UUID.randomUUID();

        productApiService.deleteProduct(productId);

        verify(productService).deleteById(eq(productId));
    }
}
