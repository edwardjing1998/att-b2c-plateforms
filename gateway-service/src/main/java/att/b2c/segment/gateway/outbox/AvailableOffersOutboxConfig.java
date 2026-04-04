package att.b2c.segment.gateway.outbox;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AvailableOffersOutboxProperties.class)
public class AvailableOffersOutboxConfig {
}
