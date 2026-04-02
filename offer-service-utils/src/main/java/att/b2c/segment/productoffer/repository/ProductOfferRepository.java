package att.b2c.segment.productoffer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import att.b2c.segment.productoffer.ProductOffer;
import att.b2c.segment.productoffer.ProductOfferKey;

@Repository
public interface ProductOfferRepository extends CassandraRepository<ProductOffer, ProductOfferKey> {

    List<ProductOffer> findByKeyProductId(UUID productId);
}
