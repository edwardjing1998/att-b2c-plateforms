package att.b2c.segment.productoffer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import att.b2c.segment.productoffer.Offer;

@Repository
public interface OfferRepository extends CassandraRepository<Offer, UUID> {

    List<Offer> findByZip(String zip);
}
