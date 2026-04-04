package att.b2c.segment.shared.outbox.availableoffers;

import java.time.Instant;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("available_offers_outbox")
public class AvailableOffersOutboxRecord {

    @PrimaryKey
    private AvailableOffersOutboxKey key;

    @Column("customer_id")
    private String customerId;

    private String payload;
    private String topic;

    @Column("key")
    private String keyValue;

    private Integer attempts;

    @Column("last_error")
    private String lastError;

    @Column("sent_at")
    private Instant sentAt;
}
