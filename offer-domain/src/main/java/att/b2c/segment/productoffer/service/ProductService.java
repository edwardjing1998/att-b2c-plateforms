package att.b2c.segment.productoffer.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import att.b2c.segment.productoffer.Product;
import att.b2c.segment.productoffer.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(UUID productId) {
        return productRepository.findById(productId).orElse(null);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAllById(Iterable<UUID> productIds) {
        return productRepository.findAllById(productIds);
    }

    public void deleteById(UUID productId) {
        productRepository.deleteById(productId);
    }
}
