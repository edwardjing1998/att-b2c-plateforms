package att.b2c.segment.productoffer.repository;

import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import att.b2c.segment.productoffer.Product;

@Repository
public interface ProductRepository extends CassandraRepository<Product, UUID> {
}
