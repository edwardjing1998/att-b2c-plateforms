package att.b2c.segment.productoffer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import att.b2c.segment.productoffer.dto.ProductResponse;
import att.b2c.segment.productoffer.dto.UpsertProductRequest;
import att.b2c.segment.productoffer.error.NotFoundException;
import att.b2c.segment.productoffer.service.ProductApiService;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductApiService productApiService;

    @InjectMocks
    private ProductController productController;

    @Test
    void getAllProducts_returnsProducts() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductResponse dto = ProductResponse.builder().productId(productId).name("p1").description("d1").price(BigDecimal.TEN).build();

        when(productApiService.getProducts()).thenReturn(List.of(dto));

        ResponseEntity<List<ProductResponse>> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(productId, response.getBody().get(0).getProductId());
    }

    @Test
    void getProduct_whenMissing_throwsNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        when(productApiService.getProduct(eq(productId))).thenThrow(new NotFoundException("Product", productId));

        assertThrows(NotFoundException.class, () -> productController.getProduct(productId));
    }

    @Test
    void upsertProduct_returnsSavedDto() throws Exception {
        UUID productId = UUID.randomUUID();
        UpsertProductRequest request = UpsertProductRequest.builder().productId(productId).name("p1").description("d1").price(BigDecimal.TEN).build();
        ProductResponse response = ProductResponse.builder().productId(productId).name("p1").description("d1").price(BigDecimal.TEN).build();

        when(productApiService.upsertProduct(eq(request))).thenReturn(response);

        ResponseEntity<ProductResponse> entity = productController.upsertProduct(request);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(productId, entity.getBody().getProductId());
    }

    @Test
    void deleteProduct_returns204() throws Exception {
        UUID productId = UUID.randomUUID();

        ResponseEntity<Void> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productApiService).deleteProduct(eq(productId));
    }
}
