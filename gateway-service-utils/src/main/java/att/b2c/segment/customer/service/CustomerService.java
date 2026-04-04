package att.b2c.segment.customer.service;

import att.b2c.segment.customer.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    private final WebClient webClient;

    public CustomerService(@Value("${customer.api-url:http://localhost:7777/api}") String customerApiUrl) {
        this.webClient = WebClient.builder().baseUrl(customerApiUrl).build();
    }

    public Mono<CustomerDto> getCustomerById(String customerId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("customers/{customerId}").build(customerId))
                .retrieve()
                .bodyToMono(CustomerDto.class);
    }
}