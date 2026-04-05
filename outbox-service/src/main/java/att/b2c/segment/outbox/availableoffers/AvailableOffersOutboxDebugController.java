package att.b2c.segment.outbox.availableoffers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxDao;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxRecord;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxStatus;
import att.b2c.segment.shared.outbox.availableoffers.AvailableOffersOutboxProperties;

@RestController
@RequestMapping("/debug/outbox/available-offers")
public class AvailableOffersOutboxDebugController {

    private final AvailableOffersOutboxDao dao;
    private final AvailableOffersOutboxProperties properties;

    public AvailableOffersOutboxDebugController(AvailableOffersOutboxDao dao, AvailableOffersOutboxProperties properties) {
        this.dao = dao;
        this.properties = properties;
    }

    @GetMapping
    public Map<String, List<AvailableOffersOutboxRecord>> byCustomerId(@RequestParam("customerId") String customerId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        int bucket = bucket(customerId);

        Map<String, List<AvailableOffersOutboxRecord>> out = new LinkedHashMap<>();
        out.put(AvailableOffersOutboxStatus.NEW,
                filterCustomerId(dao.fetch(AvailableOffersOutboxStatus.NEW, bucket, limit), customerId));
        out.put(AvailableOffersOutboxStatus.SENT,
                filterCustomerId(dao.fetch(AvailableOffersOutboxStatus.SENT, bucket, limit), customerId));
        out.put(AvailableOffersOutboxStatus.FAILED,
                filterCustomerId(dao.fetch(AvailableOffersOutboxStatus.FAILED, bucket, limit), customerId));
        return out;
    }

    private List<AvailableOffersOutboxRecord> filterCustomerId(List<AvailableOffersOutboxRecord> records, String customerId) {
        if (records == null || customerId == null) {
            return List.of();
        }
        return records.stream().filter(r -> customerId.equals(r.getCustomerId())).collect(Collectors.toList());
    }

    private int bucket(String customerId) {
        return Math.floorMod(customerId.hashCode(), properties.getBucketCount());
    }
}
