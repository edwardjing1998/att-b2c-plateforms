package att.b2c.segment.gateway.outbox;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import att.b2c.segment.customer.dto.AvailableOffersEventDto;
import att.b2c.segment.customer.dto.CustomerOffersDto;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxProperties;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxDao;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxKey;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxRecord;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxStatus;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class AvailableOffersOutboxService {

    private final AvailableOffersOutboxDao dao;
    private final AvailableOffersOutboxProperties properties;
    private final ObjectMapper objectMapper;
    private final AvailableOffersOutboxMapper mapper;

    public AvailableOffersOutboxService(AvailableOffersOutboxDao dao,
            AvailableOffersOutboxProperties properties,
            ObjectMapper objectMapper,
            AvailableOffersOutboxMapper mapper) {
        this.dao = dao;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
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

        AvailableOffersEventDto eventDto = mapper.toEventDto(eventId, createdAt, dto);

        String payload;
        try {
            payload = objectMapper.writeValueAsString(eventDto);
        } catch (JacksonException e) {
            return Mono.error(e);
        }

        AvailableOffersOutboxKey key = mapper.toKey(
                AvailableOffersOutboxStatus.NEW,
                bucket,
                createdAt,
                eventId);

        AvailableOffersOutboxRecord record = mapper.toNewRecord(
                key,
                customerId,
                payload,
                properties.getTopic(),
                customerId);

        return Mono.fromRunnable(() -> dao.insert(record)).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public int bucket(String customerId) {
        return Math.floorMod(customerId.hashCode(), properties.getBucketCount());
    }
}
