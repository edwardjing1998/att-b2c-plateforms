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

import att.b2c.segment.productoffer.dto.OfferResponse;
import att.b2c.segment.productoffer.dto.ProductSummaryDto;
import att.b2c.segment.productoffer.dto.UpsertOfferRequest;
import att.b2c.segment.productoffer.service.OfferApiService;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferApiService offerApiService;

    public OfferController(OfferApiService offerApiService) {
        this.offerApiService = offerApiService;
    }

    @GetMapping
    public ResponseEntity<List<OfferResponse>> getAllOffers(
            @RequestParam(name = "productId", required = false) UUID productId,
            @RequestParam(name = "zip", required = false) String zip) {
        return ResponseEntity.ok(offerApiService.getOffers(productId, zip));
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<OfferResponse> getOffer(@PathVariable UUID offerId) {
        return ResponseEntity.ok(offerApiService.getOffer(offerId));
    }

    @GetMapping("/{offerId}/products")
    public ResponseEntity<List<ProductSummaryDto>> getProductsByOfferId(@PathVariable UUID offerId) {
        return ResponseEntity.ok(offerApiService.getProductsByOfferId(offerId));
    }

    @PostMapping
    public ResponseEntity<OfferResponse> upsertOffer(@RequestBody UpsertOfferRequest request) {
        return ResponseEntity.ok(offerApiService.upsertOffer(request));
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<Void> deleteOffer(@PathVariable UUID offerId) {
        offerApiService.deleteOffer(offerId);
        return ResponseEntity.noContent().build();
    }
}
