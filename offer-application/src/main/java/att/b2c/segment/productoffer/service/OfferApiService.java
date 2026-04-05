package att.b2c.segment.productoffer.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import att.b2c.segment.productoffer.Offer;
import att.b2c.segment.productoffer.Product;
import att.b2c.segment.productoffer.dto.OfferResponse;
import att.b2c.segment.productoffer.dto.ProductSummaryDto;
import att.b2c.segment.productoffer.dto.UpsertOfferRequest;
import att.b2c.segment.productoffer.error.NotFoundException;
import att.b2c.segment.productoffer.mapping.OfferMapper;
import att.b2c.segment.productoffer.mapping.ProductSummaryMapper;

@Service
public class OfferApiService {

    private final OfferService offerService;
    private final OfferMapper offerMapper;
    private final ProductSummaryMapper productSummaryMapper;

    public OfferApiService(OfferService offerService, OfferMapper offerMapper, ProductSummaryMapper productSummaryMapper) {
        this.offerService = offerService;
        this.offerMapper = offerMapper;
        this.productSummaryMapper = productSummaryMapper;
    }

    public List<OfferResponse> getOffers(UUID productId, String zip) {
        List<Offer> offers;
        if (productId != null && zip != null && !zip.isBlank()) {
            offers = offerService.findByProductIdAndZip(productId, zip);
        } else if (productId != null) {
            offers = offerService.findByProductId(productId);
        } else if (zip != null && !zip.isBlank()) {
            offers = offerService.findByZip(zip);
        } else {
            offers = offerService.findAll();
        }

        List<UUID> offerIds = offers.stream().map(Offer::getOfferId).toList();
        Map<UUID, List<Product>> productsByOfferId = offerService.findProductsByOfferIds(offerIds);

        return offers.stream().map(offer -> {
            OfferResponse dto = offerMapper.toResponse(offer);
            List<ProductSummaryDto> products = productsByOfferId.getOrDefault(offer.getOfferId(), List.of()).stream()
                    .map(productSummaryMapper::toDto)
                    .toList();
            dto.setProducts(products);
            return dto;
        }).toList();
    }

    public OfferResponse getOffer(UUID offerId) {
        Offer offer = offerService.findById(offerId);
        if (offer == null) {
            throw new NotFoundException("Offer", offerId);
        }
        OfferResponse dto = offerMapper.toResponse(offer);
        List<ProductSummaryDto> products = getProductsByOfferId(offerId);
        dto.setProducts(products);
        return dto;
    }

    public List<ProductSummaryDto> getProductsByOfferId(UUID offerId) {
        return offerService.findProductsByOfferId(offerId).stream().map(productSummaryMapper::toDto).toList();
    }

    public OfferResponse upsertOffer(UpsertOfferRequest request) {
        Offer saved = offerService.save(offerMapper.toModel(request));
        return offerMapper.toResponse(saved);
    }

    public void deleteOffer(UUID offerId) {
        offerService.deleteById(offerId);
    }
}
