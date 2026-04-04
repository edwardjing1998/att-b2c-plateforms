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