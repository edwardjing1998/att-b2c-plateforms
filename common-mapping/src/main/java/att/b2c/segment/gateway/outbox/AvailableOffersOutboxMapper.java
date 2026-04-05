package att.b2c.segment.gateway.outbox;

import java.time.Instant;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import att.b2c.segment.customer.dto.AvailableOffersEventDto;
import att.b2c.segment.customer.dto.CustomerOffersDto;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxKey;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxRecord;

@Mapper(componentModel = "spring")
public interface AvailableOffersOutboxMapper {

    @Mapping(target = "payload", source = "dto")
    AvailableOffersEventDto toEventDto(UUID eventId, Instant createdAt, CustomerOffersDto dto);

    AvailableOffersOutboxKey toKey(String status, Integer bucket, Instant createdAt, UUID eventId);

    @Mapping(target = "attempts", constant = "0")
    @Mapping(target = "lastError", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    AvailableOffersOutboxRecord toNewRecord(AvailableOffersOutboxKey key, String customerId, String payload, String topic,
            String keyValue);
}
