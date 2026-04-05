# utility commands:
mvn spring-boot:run

http://localhost:8888/swagger-ui/index.html

# casandra database:
casandra cql statements:

DESCRIBE TABLES;

INSERT INTO product (productId, name, description, price) VALUES (uuid(), 'Product 1', 'Description for product 1', 9.99);
INSERT INTO product (productId, name, description, price) VALUES (uuid(), 'Product 2', 'Description for product 2', 19.99);
INSERT INTO product (productId, name, description, price) VALUES (uuid(), 'Product 3', 'Description for product 3', 29.99);

INSERT INTO offer (offerId, name, productId) VALUES (uuid(), 'Offer 1', 81b8949d-d10f-4edd-8a78-60d36753c3fb);
INSERT INTO offer (offerId, name, productId) VALUES (uuid(), 'Offer 2', 32407e58-bb2e-4935-9167-858220baf833);
INSERT INTO offer (offerId, name, productId) VALUES (uuid(), 'Offer 3', 2f20aeb6-106f-47ef-800c-3ddd02999d64);

INSERT INTO offer (offerId, name, productId) VALUES (uuid(), 'Offer 1', 2f20aeb6-106f-47ef-800c-3ddd02999d64);
INSERT INTO offer (offerId, name, productId) VALUES (uuid(), 'Offer 2', 81b8949d-d10f-4edd-8a78-60d36753c3fb);
INSERT INTO offer (offerId, name, productId) VALUES (uuid(), 'Offer 3', 81b8949d-d10f-4edd-8a78-60d36753c3fb);


CREATE TABLE IF NOT EXISTS available_offers_outbox (
  status text,              -- 'NEW', 'SENT', 'FAILED'
  bucket int,               -- 0..N-1 to distribute load
  created_at timestamp,     -- clustering for ordering
  event_id uuid,            -- unique id
  customer_id text,
  payload text,             -- JSON string
  topic text,
  key text,                 -- kafka key, e.g. customer_id
  attempts int,
  last_error text,
  sent_at timestamp,
  PRIMARY KEY ((status, bucket), created_at, event_id)
) WITH CLUSTERING ORDER BY (created_at ASC);




# install redis:

brew install redis

brew services start redis

## run redis client
redis-cli

## search records in redis:
GET customer::yourCustomerId

# install kafka:

# install kafka:

## brew install kafka



To restart kafka after an upgrade:
brew services restart kafka
Or, if you don't want/need a background service you can just run:
/usr/local/opt/kafka/bin/kafka-server-start /usr/local/etc/kafka/server.properties

To start Kafka that's installed without Zookeeper, you can use the command that was printed after the installation:
/usr/local/opt/kafka/bin/kafka-server-start /usr/local/etc/kafka/server.properties

## cd /usr/local/opt/kafka

## create topic:

bin/kafka-topics.sh --create --topic place-order --bootstrap-server localhost:9092

## run kafka producer:
### first: bin/kafka-console-producer --bootstrap-server localhost:9092 --topic place-order
### then: {"orderId":"ORD-100001","customerId":"CUST-1001","productId":"PROD-2001","productName":"Fiber Internet 1G","quantity":1,"address":"123 Main St, Dallas, TX 75001","status":"RECEIVED","channel":"E_COMMERCE","sourceSystem":"gateway-service","orderDateTime":"2026-03-29T15:30:00"}

## run kafka consumer:
bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic place-order --from-beginning




Phase 1 — Fix the N+1 query in OfferApiService.getOffers(...) (highest ROI)
Identify current N+1 call site
It’s in OfferApiService.getOffers(...) where each offer calls findProductsByOfferId(...).
Add a batch method in the domain layer
In offer-service-utils, add something like:
List<OfferProduct> findByOfferIds(Set<UUID> offerIds) in OfferProductRepository (or service)
Then translate links → productIds → products in one pass
Add a domain/service method that returns grouped results
e.g. Map<UUID, List<Product>> findProductsByOfferIds(Set<UUID> offerIds)
Update OfferApiService.getOffers(...)
Fetch offers once
Batch fetch products for all offerIds
Attach products per offer DTO via the grouped map
Verify
Run mvn -pl offer-service test (or root mvn test)
Manually call GET /api/offers and ensure response unchanged


Phase 2 — Standardize “not found” handling (removes null checks)
Pick an approach
Option A (simple): return Optional<OfferDto> / Optional<ProductDto> from ApiService
Option B (cleaner HTTP): throw NotFoundException and handle via @ControllerAdvice
Implement chosen approach
If Option B:
Create NotFoundException
Create GlobalExceptionHandler with @ExceptionHandler(NotFoundException.class) returning 404
Simplify controllers
Remove if (dto == null) blocks
Verify
Ensure missing IDs still result in 404


Phase 3 — Split request/response DTOs (prevents “ignored fields”)
Introduce request DTOs
UpsertOfferRequest (fields needed to save)
UpsertProductRequest
Introduce response DTOs
OfferResponse (includes products)
ProductResponse (includes offers if you plan that)
Update controllers
@RequestBody UpsertOfferRequest instead of OfferDto
Update ApiServices and mappers
Add MapStruct mappers for request/response or map manually
Verify
Existing clients may break—if you need backward compatibility, version endpoints (/v2/...) or keep old DTOs temporarily


Phase 4 — Clarify module boundaries (optional “architecture cleanup”)
Decide whether offer-service-utils is the right home
If yes: consider renaming module later (purely organizational)
If no: split into:
offer-domain (repositories/entities/services)
offer-application (ApiService + mapping)
offer-service (Spring Boot app)
Enforce dependency direction
offer-service → offer-application → offer-domain
Domain should not depend on web DTOs unless you intentionally allow that
Verify
mvn clean install at root
