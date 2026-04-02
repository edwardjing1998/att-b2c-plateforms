package att.b2c.segment.customer.controller;

import att.b2c.segment.customer.service.CustomerService;
import att.b2c.segment.customer.dto.CustomerDto;
import att.b2c.segment.customer.dto.CustomerOffersDto;
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

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final OfferClientService offerClientService;

    @Autowired
    public CustomerController(CustomerService customerService, OfferClientService offerClientService) {
        this.customerService = customerService;
        this.offerClientService = offerClientService;
    }

    @GetMapping("/{customerId}")
    public Mono<CustomerDto> getCustomerById(@PathVariable String customerId) {
        return customerService.getCustomerById(customerId);
    }

    @GetMapping("/{customerId}/offers")
    public Mono<CustomerOffersDto> getCustomerOffers(@PathVariable String customerId,
            @RequestParam(name = "productId", required = false) UUID productId) {
        Mono<CustomerDto> customerMono = customerService.getCustomerById(customerId)
                .timeout(Duration.ofSeconds(3))
                .onErrorMap(TimeoutException.class,
                        ex -> new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "Customer service timeout", ex))
                .onErrorMap(WebClientResponseException.NotFound.class,
                        ex -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found", ex));

        Mono<List<OfferDto>> offersMono = offerClientService.getAllOffers(productId)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(TimeoutException.class, ex -> Mono.just(List.of()))
                .onErrorResume(ex -> Mono.just(List.of()));

        return Mono.zip(customerMono, offersMono, CustomerOffersDto::new);
    }
}
