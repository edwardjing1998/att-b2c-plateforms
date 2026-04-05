package att.b2c.segment.productoffer.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import att.b2c.segment.productoffer.dto.OfferResponse;
import att.b2c.segment.productoffer.service.OfferClientService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferClientService offerClientService;

    public OfferController(OfferClientService offerClientService) {
        this.offerClientService = offerClientService;
    }

    @GetMapping
    public Mono<List<OfferResponse>> getAllOffers(
            @RequestParam(name = "productId", required = false) UUID productId,
            @RequestParam(name = "zip", required = false) String zip) {
        return offerClientService.getAllOffers(productId, zip);
    }
}
