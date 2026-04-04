package att.b2c.segment.outbox.availableoffers;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxProperties;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(AvailableOffersOutboxProperties.class)
public class AvailableOffersOutboxConfig {
}
