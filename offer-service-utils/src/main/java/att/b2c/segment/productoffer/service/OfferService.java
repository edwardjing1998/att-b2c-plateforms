package att.b2c.segment.productoffer.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import att.b2c.segment.productoffer.Offer;
import att.b2c.segment.productoffer.OfferProduct;
import att.b2c.segment.productoffer.Product;
import att.b2c.segment.productoffer.repository.OfferRepository;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final OfferProductService offerProductService;
    private final ProductService productService;

    public OfferService(OfferRepository offerRepository, OfferProductService offerProductService, ProductService productService) {
        this.offerRepository = offerRepository;
        this.offerProductService = offerProductService;
        this.productService = productService;
    }

    public List<Offer> findAll() {
        return offerRepository.findAll();
    }

    public List<Offer> findByZip(String zip) {
        if (zip == null || zip.isBlank()) {
            return List.of();
        }
        return offerRepository.findByZip(zip);
    }

    public Offer findById(UUID offerId) {
        return offerRepository.findById(offerId).orElse(null);
    }

    public List<Offer> findByProductId(UUID productId) {
        List<OfferProduct> links = offerProductService.findByProductId(productId);
        List<UUID> offerIds = links.stream().map(link -> link.getKey().getOfferId()).distinct().collect(Collectors.toList());
        return offerIds.isEmpty() ? List.of() : offerRepository.findAllById(offerIds);
    }

    public List<Offer> findByProductIdAndZip(UUID productId, String zip) {
        return findByProductId(productId).stream()
                .filter(o -> zip != null && zip.equals(o.getZip()))
                .toList();
    }

    public List<Product> findProductsByOfferId(UUID offerId) {
        List<OfferProduct> links = offerProductService.findByOfferId(offerId);
        List<UUID> productIds = links.stream().map(link -> link.getKey().getProductId()).collect(Collectors.toList());
        return productService.findAllById(productIds);
    }

    public Offer save(Offer offer) {
        return offerRepository.save(offer);
    }

    public void deleteById(UUID offerId) {
        offerRepository.deleteById(offerId);
    }
}
