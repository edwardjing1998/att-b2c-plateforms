package att.b2c.segment.productoffer.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import att.b2c.segment.productoffer.dto.ProductResponse;
import att.b2c.segment.productoffer.dto.UpsertProductRequest;
import att.b2c.segment.productoffer.service.ProductApiService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductApiService productApiService;

    public ProductController(ProductApiService productApiService) {
        this.productApiService = productApiService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productApiService.getProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(productApiService.getProduct(productId));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> upsertProduct(@RequestBody UpsertProductRequest request) {
        return ResponseEntity.ok(productApiService.upsertProduct(request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        productApiService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
