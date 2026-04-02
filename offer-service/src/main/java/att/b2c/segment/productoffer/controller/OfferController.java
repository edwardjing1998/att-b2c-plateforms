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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import att.b2c.segment.productoffer.Offer;
import att.b2c.segment.productoffer.Product;
import att.b2c.segment.productoffer.dto.OfferDto;
import att.b2c.segment.productoffer.dto.ProductSummaryDto;
import att.b2c.segment.productoffer.mapping.OfferMapper;
import att.b2c.segment.productoffer.mapping.ProductSummaryMapper;
import att.b2c.segment.productoffer.service.OfferService;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;
    private final OfferMapper offerMapper;
    private final ProductSummaryMapper productSummaryMapper;

    public OfferController(OfferService offerService, OfferMapper offerMapper, ProductSummaryMapper productSummaryMapper) {
        this.offerService = offerService;
        this.offerMapper = offerMapper;
        this.productSummaryMapper = productSummaryMapper;
    }

    @GetMapping
    public ResponseEntity<List<OfferDto>> getAllOffers(@RequestParam(name = "productId", required = false) UUID productId) {
        List<Offer> offers = productId == null ? offerService.findAll() : offerService.findByProductId(productId);
        List<OfferDto> dtos = offers.stream().map(offer -> {
            OfferDto dto = offerMapper.toDto(offer);
            List<ProductSummaryDto> products = offerService.findProductsByOfferId(offer.getOfferId()).stream().map(productSummaryMapper::toDto)
                    .toList();
            dto.setProducts(products);
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<OfferDto> getOffer(@PathVariable UUID offerId) {
        Offer offer = offerService.findById(offerId);
        if (offer == null) {
            return ResponseEntity.notFound().build();
        }
        OfferDto dto = offerMapper.toDto(offer);
        List<ProductSummaryDto> products = offerService.findProductsByOfferId(offerId).stream().map(productSummaryMapper::toDto).toList();
        dto.setProducts(products);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{offerId}/products")
    public ResponseEntity<List<ProductSummaryDto>> getProductsByOfferId(@PathVariable UUID offerId) {
        List<Product> products = offerService.findProductsByOfferId(offerId);
        List<ProductSummaryDto> dtos = products.stream().map(productSummaryMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<OfferDto> upsertOffer(@RequestBody OfferDto dto) {
        Offer saved = offerService.save(offerMapper.toModel(dto));
        return ResponseEntity.ok(offerMapper.toDto(saved));
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<Void> deleteOffer(@PathVariable UUID offerId) {
        offerService.deleteById(offerId);
        return ResponseEntity.noContent().build();
    }
}
