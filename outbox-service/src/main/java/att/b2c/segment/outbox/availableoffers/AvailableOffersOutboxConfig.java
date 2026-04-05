package att.b2c.segment.outbox.availableoffers;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxProperties;

@Configuration
@EnableScheduling
public class AvailableOffersOutboxConfig {

    @Bean(name = "availableOffersOutboxProperties")
    @ConfigurationProperties(prefix = "available-offers.outbox")
    public AvailableOffersOutboxProperties availableOffersOutboxProperties() {
        return new AvailableOffersOutboxProperties();
    }
}
