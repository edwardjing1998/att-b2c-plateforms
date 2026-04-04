package att.b2c.segment.shared.outbox.availableoffers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

@Component
public class AvailableOffersOutboxDao {

    private final CqlSession session;

    private final PreparedStatement select;
    private final PreparedStatement insert;
    private final PreparedStatement delete;
    private final PreparedStatement updateAttemptsError;

    public AvailableOffersOutboxDao(CqlSession session) {
        this.session = session;

        this.select = session.prepare(
                "SELECT status, bucket, created_at, event_id, customer_id, payload, topic, \"key\", attempts, last_error, sent_at "
                        + "FROM available_offers_outbox "
                        + "WHERE status = ? AND bucket = ?");

        this.insert = session.prepare(
                "INSERT INTO available_offers_outbox "
                        + "(status, bucket, created_at, event_id, customer_id, payload, topic, \"key\", attempts, last_error, sent_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        this.delete = session.prepare(
                "DELETE FROM available_offers_outbox "
                        + "WHERE status = ? AND bucket = ? AND created_at = ? AND event_id = ?");

        this.updateAttemptsError = session.prepare(
                "UPDATE available_offers_outbox SET attempts = ?, last_error = ? "
                        + "WHERE status = ? AND bucket = ? AND created_at = ? AND event_id = ?");
    }

    public List<AvailableOffersOutboxRecord> fetch(String status, int bucket, int limit) {
        BoundStatement stmt = select.bind(status, bucket).setPageSize(limit);
        ResultSet rs = session.execute(stmt);
        List<AvailableOffersOutboxRecord> out = new ArrayList<>();
        for (Row row : rs) {
            out.add(map(row));
        }
        return out;
    }

    public void insert(AvailableOffersOutboxRecord record) {
        AvailableOffersOutboxKey k = record.getKey();
        session.execute(insert.bind(
                k.getStatus(),
                k.getBucket(),
                k.getCreatedAt(),
                k.getEventId(),
                record.getCustomerId(),
                record.getPayload(),
                record.getTopic(),
                record.getKeyValue(),
                record.getAttempts(),
                record.getLastError(),
                record.getSentAt()));
    }

    public void delete(AvailableOffersOutboxKey key) {
        session.execute(delete.bind(key.getStatus(), key.getBucket(), key.getCreatedAt(), key.getEventId()));
    }

    public void updateAttemptsAndError(AvailableOffersOutboxKey key, int attempts, String lastError) {
        session.execute(updateAttemptsError.bind(attempts, lastError, key.getStatus(), key.getBucket(), key.getCreatedAt(),
                key.getEventId()));
    }

    private AvailableOffersOutboxRecord map(Row row) {
        String status = row.getString("status");
        Integer bucket = row.getInt("bucket");
        Instant createdAt = row.getInstant("created_at");
        UUID eventId = row.getUuid("event_id");

        AvailableOffersOutboxKey key = new AvailableOffersOutboxKey(status, bucket, createdAt, eventId);

        String customerId = row.getString("customer_id");
        String payload = row.getString("payload");
        String topic = row.getString("topic");
        String keyValue = row.getString("key");
        Integer attempts = row.isNull("attempts") ? null : row.getInt("attempts");
        String lastError = row.getString("last_error");
        Instant sentAt = row.getInstant("sent_at");

        return new AvailableOffersOutboxRecord(key, customerId, payload, topic, keyValue, attempts, lastError, sentAt);
    }
}
