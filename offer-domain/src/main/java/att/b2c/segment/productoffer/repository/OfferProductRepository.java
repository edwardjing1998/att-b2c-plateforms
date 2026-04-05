package att.b2c.segment.productoffer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import att.b2c.segment.productoffer.OfferProduct;
import att.b2c.segment.productoffer.OfferProductKey;

@Repository
public interface OfferProductRepository extends CassandraRepository<OfferProduct, OfferProductKey> {

    List<OfferProduct> findByKeyOfferId(UUID offerId);

    List<OfferProduct> findByKeyOfferIdIn(List<UUID> offerIds);

    List<OfferProduct> findByKeyProductId(UUID productId);
}
