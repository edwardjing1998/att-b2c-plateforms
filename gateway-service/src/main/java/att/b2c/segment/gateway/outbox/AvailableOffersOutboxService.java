package att.b2c.segment.gateway.outbox;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import att.b2c.segment.customer.dto.AvailableOffersEventDto;
import att.b2c.segment.customer.dto.CustomerOffersDto;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxDao;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxKey;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxRecord;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class AvailableOffersOutboxService {

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_FAILED = "FAILED";

    private final AvailableOffersOutboxDao dao;
    private final AvailableOffersOutboxProperties properties;
    private final ObjectMapper objectMapper;

    public AvailableOffersOutboxService(AvailableOffersOutboxDao dao,
            AvailableOffersOutboxProperties properties,
            ObjectMapper objectMapper) {
        this.dao = dao;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> enqueue(CustomerOffersDto dto) {
        if (dto == null || dto.getCustomer() == null || dto.getCustomer().getCustomerId() == null
                || dto.getCustomer().getCustomerId().isBlank()) {
            return Mono.empty();
        }

        String customerId = dto.getCustomer().getCustomerId();
        int bucket = bucket(customerId);

        UUID eventId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        AvailableOffersEventDto eventDto = new AvailableOffersEventDto(eventId, createdAt, dto);

        String payload;
        try {
            payload = objectMapper.writeValueAsString(eventDto);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }

        AvailableOffersOutboxKey key = new AvailableOffersOutboxKey(
                STATUS_NEW,
                bucket,
                createdAt,
                eventId);

        AvailableOffersOutboxRecord record = new AvailableOffersOutboxRecord(
                key,
                customerId,
                payload,
                properties.getTopic(),
                customerId,
                0,
                null,
                null);

        return Mono.fromRunnable(() -> dao.insert(record)).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public int bucket(String customerId) {
        return Math.floorMod(customerId.hashCode(), properties.getBucketCount());
    }
}
