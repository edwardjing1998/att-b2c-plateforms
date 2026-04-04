package att.b2c.segment.customer.controller;

import att.b2c.segment.customer.service.CustomerService;
import att.b2c.segment.customer.dto.CustomerDto;
import att.b2c.segment.customer.dto.CustomerOffersDto;
import att.b2c.segment.gateway.outbox.AvailableOffersOutboxService;
import att.b2c.segment.productoffer.dto.OfferDto;
import att.b2c.segment.productoffer.service.OfferClientService;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/customers")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final OfferClientService offerClientService;
    private final AvailableOffersOutboxService availableOffersOutboxService;

    @Autowired
    public CustomerController(CustomerService customerService,
            OfferClientService offerClientService,
            AvailableOffersOutboxService availableOffersOutboxService) {
        this.customerService = customerService;
        this.offerClientService = offerClientService;
        this.availableOffersOutboxService = availableOffersOutboxService;
    }

    @GetMapping("/{customerId}")
    public Mono<CustomerDto> getCustomerById(@PathVariable String customerId) {
        return customerService.getCustomerById(customerId);
    }

    @GetMapping("/{customerId}/offers")
    public Mono<CustomerOffersDto> getCustomerOffers(@PathVariable String customerId,
            @RequestParam(name = "productId", required = false) UUID productId,
            @RequestParam(name = "zip", required = false) String zip) {
        Mono<CustomerDto> customerMono = customerService.getCustomerById(customerId)
                .timeout(Duration.ofSeconds(3))
                .onErrorMap(TimeoutException.class,
                        ex -> new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "Customer service timeout", ex))
                .onErrorMap(WebClientResponseException.NotFound.class,
                        ex -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found", ex));

        Mono<List<OfferDto>> offersMono = customerMono
                .map(customer -> (zip == null || zip.isBlank()) ? customer.getZip() : zip)
                .flatMap(resolvedZip -> offerClientService.getAllOffers(productId, resolvedZip))
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(TimeoutException.class, ex -> Mono.just(List.of()))
                .onErrorResume(ex -> Mono.just(List.of()));

        return Mono.zip(customerMono, offersMono, CustomerOffersDto::new)
                .flatMap(dto -> availableOffersOutboxService.enqueue(dto)
                        .doOnError(ex -> log.warn("Failed to enqueue available offers outbox record", ex))
                        .onErrorResume(ex -> Mono.empty())
                        .thenReturn(dto));
    }
}
