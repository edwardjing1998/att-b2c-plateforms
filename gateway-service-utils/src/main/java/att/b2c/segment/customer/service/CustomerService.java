package att.b2c.segment.customer.service;

import att.b2c.segment.customer.dto.CustomerDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    private static final String API_URL = "http://localhost:7777/api/";
    private final WebClient webClient;

    public CustomerService() {
        this.webClient = WebClient.builder().baseUrl(API_URL).build();
    }

    public Mono<CustomerDto> getCustomerById(String customerId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("customers/{customerId}").build(customerId))
                .retrieve()
                .bodyToMono(CustomerDto.class);
    }
}