package att.b2c.segment.productoffer.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import att.b2c.segment.productoffer.OfferProduct;
import att.b2c.segment.productoffer.repository.OfferProductRepository;

@Service
public class OfferProductService {

    private final OfferProductRepository offerProductRepository;

    public OfferProductService(OfferProductRepository offerProductRepository) {
        this.offerProductRepository = offerProductRepository;
    }

    public List<OfferProduct> findByOfferId(UUID offerId) {
        return offerProductRepository.findByKeyOfferId(offerId);
    }

    public List<OfferProduct> findByOfferIds(List<UUID> offerIds) {
        if (offerIds == null || offerIds.isEmpty()) {
            return List.of();
        }
        return offerProductRepository.findByKeyOfferIdIn(offerIds);
    }

    public List<OfferProduct> findByProductId(UUID productId) {
        return offerProductRepository.findByKeyProductId(productId);
    }
}
