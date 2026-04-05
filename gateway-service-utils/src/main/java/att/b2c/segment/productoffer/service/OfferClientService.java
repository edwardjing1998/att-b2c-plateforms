package att.b2c.segment.productoffer.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import att.b2c.segment.productoffer.dto.OfferResponse;
import reactor.core.publisher.Mono;

@Service
public class OfferClientService {

    private final WebClient webClient;

    public OfferClientService(@Value("${product-offer.api-url:http://localhost:9999}") String productOfferApiUrl) {
        this.webClient = WebClient.builder().baseUrl(productOfferApiUrl).build();
    }

    public Mono<List<OfferResponse>> getAllOffers(UUID productId) {
        return getAllOffers(productId, null);
    }

    public Mono<List<OfferResponse>> getAllOffers(UUID productId, String zip) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/offers");
                    if (productId != null) {
                        uriBuilder.queryParam("productId", productId);
                    }
                    if (zip != null && !zip.isBlank()) {
                        uriBuilder.queryParam("zip", zip);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<OfferResponse>>() {
                });
    }
}
