package att.b2c.segment.productoffer.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import att.b2c.segment.productoffer.ProductOffer;
import att.b2c.segment.productoffer.repository.ProductOfferRepository;

@Service
public class ProductOfferService {

    private final ProductOfferRepository productOfferRepository;

    public ProductOfferService(ProductOfferRepository productOfferRepository) {
        this.productOfferRepository = productOfferRepository;
    }

    public List<ProductOffer> findByProductId(UUID productId) {
        return productOfferRepository.findByKeyProductId(productId);
    }
}
