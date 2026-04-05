package att.b2c.segment.outbox.availableoffers;

import java.time.Instant;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxDao;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxKey;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxProperties;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "available-offers.outbox.publisher", name = "enabled", havingValue = "true")
public class AvailableOffersOutboxPublisher {

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_FAILED = "FAILED";

    private final AvailableOffersOutboxDao dao;
    private final AvailableOffersOutboxProperties properties;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public AvailableOffersOutboxPublisher(AvailableOffersOutboxDao dao,
            AvailableOffersOutboxProperties properties,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.dao = dao;
        this.properties = properties;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(initialDelayString = "#{@availableOffersOutboxProperties.pollDelayMs}",fixedDelayString = "#{@availableOffersOutboxProperties.pollDelayMs}")
    public void publishNew() {
        for (int bucket = 0; bucket < properties.getBucketCount(); bucket++) {
            List<AvailableOffersOutboxRecord> batch = dao.fetch(STATUS_NEW, bucket, properties.getBatchSize());
            if (batch.isEmpty()) {
                continue;
            }

            for (AvailableOffersOutboxRecord record : batch) {
                try {
                    log.info("Outbox publish start status={} bucket={} eventId={} customerId={} topic={}",
                            record.getKey().getStatus(),
                            record.getKey().getBucket(),
                            record.getKey().getEventId(),
                            record.getCustomerId(),
                            record.getTopic());

                    kafkaTemplate.send(record.getTopic(), record.getKeyValue(), record.getPayload()).get();

                    log.info("Outbox publish success status={} bucket={} eventId={} customerId={} topic={}",
                            record.getKey().getStatus(),
                            record.getKey().getBucket(),
                            record.getKey().getEventId(),
                            record.getCustomerId(),
                            record.getTopic());

                    AvailableOffersOutboxKey sentKey = new AvailableOffersOutboxKey(
                            STATUS_SENT,
                            record.getKey().getBucket(),
                            record.getKey().getCreatedAt(),
                            record.getKey().getEventId());

                    AvailableOffersOutboxRecord sentRecord = new AvailableOffersOutboxRecord(
                            sentKey,
                            record.getCustomerId(),
                            record.getPayload(),
                            record.getTopic(),
                            record.getKeyValue(),
                            record.getAttempts(),
                            record.getLastError(),
                            Instant.now());

                    dao.insert(sentRecord);
                    dao.delete(record.getKey());

                    log.info("Outbox state transition complete fromStatus={} toStatus={} bucket={} eventId={} customerId={}",
                            record.getKey().getStatus(),
                            sentKey.getStatus(),
                            record.getKey().getBucket(),
                            record.getKey().getEventId(),
                            record.getCustomerId());
                } catch (Exception ex) {
                    handleFailure(record, ex);
                }
            }
        }
    }

    private void handleFailure(AvailableOffersOutboxRecord record, Exception ex) {
        int attempts = record.getAttempts() == null ? 0 : record.getAttempts();
        int nextAttempts = attempts + 1;
        String error = ex.getMessage();

        if (nextAttempts >= properties.getMaxAttempts()) {
            log.warn("Outbox publish failed permanently after {} attempts for eventId={}", nextAttempts,
                    record.getKey().getEventId(), ex);

            AvailableOffersOutboxKey failedKey = new AvailableOffersOutboxKey(
                    STATUS_FAILED,
                    record.getKey().getBucket(),
                    record.getKey().getCreatedAt(),
                    record.getKey().getEventId());

            AvailableOffersOutboxRecord failedRecord = new AvailableOffersOutboxRecord(
                    failedKey,
                    record.getCustomerId(),
                    record.getPayload(),
                    record.getTopic(),
                    record.getKeyValue(),
                    nextAttempts,
                    error,
                    null);

            dao.insert(failedRecord);
            dao.delete(record.getKey());
        } else {
            log.warn("Outbox publish failed (attempt {} of {}) for eventId={}", nextAttempts,
                    properties.getMaxAttempts(), record.getKey().getEventId(), ex);
            dao.updateAttemptsAndError(record.getKey(), nextAttempts, error);
        }
    }
}
