package att.b2c.segment.productoffer.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import att.b2c.segment.productoffer.error.NotFoundException;
import att.b2c.segment.productoffer.Product;
import att.b2c.segment.productoffer.dto.ProductResponse;
import att.b2c.segment.productoffer.dto.UpsertProductRequest;
import att.b2c.segment.productoffer.mapping.ProductMapper;

@Service
public class ProductApiService {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductApiService(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    public List<ProductResponse> getProducts() {
        return productService.findAll().stream().map(productMapper::toResponse).toList();
    }

    public ProductResponse getProduct(UUID productId) {
        Product product = productService.findById(productId);
        if (product == null) {
            throw new NotFoundException("Product", productId);
        }
        return productMapper.toResponse(product);
    }

    public ProductResponse upsertProduct(UpsertProductRequest request) {
        Product saved = productService.save(productMapper.toModel(request));
        return productMapper.toResponse(saved);
    }

    public void deleteProduct(UUID productId) {
        productService.deleteById(productId);
    }
}
