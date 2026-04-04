package att.b2c.segment.shared.outbox.availableoffers;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "available-offers.outbox")
public class AvailableOffersOutboxProperties {

    private Publisher publisher = new Publisher();

    private String topic = "available-offers";
    private int bucketCount = 16;
    private int batchSize = 100;
    private int maxAttempts = 5;
    private long pollDelayMs = 1000;

    @Data
    public static class Publisher {
        private boolean enabled = true;
    }
}
