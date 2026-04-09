# Order Processing

Backend service for processing customer orders via **gRPC**, persisting them in **MongoDB**, sending **SMS notifications via SMPP**, and exposing a **reactive REST API**.

---

## Prerequisites

| Tool | Details                                |
|---|----------------------------------------|
| JDK 17 | Required                               |
| MongoDB 6+ | Running on port `27017`                |
| SMPP Server | smppsim or other SMPP server Simulator |

---
## Configuration

All custom properties are defined in `application.yml`:

```yaml
app:
  apiPort: 9090
  grpcPort: 50051


mongo:
  uri: mongodb://localhost:27017
  database: orders

smpp:
  host: 127.0.0.1
  port: 2775
  systemId: smppclient1
  password: password
```

> **Note:** MongoDB connection and WebFlux port are configured **programmatically**, not via Spring auto-configuration.

---

## Getting Started

### 1. Start MongoDB and SMPP Server

```bash
docker run -d -p 27017:27017 --name mongo mongo:latest
```
```bash
docker run -d --name smpp-server -p 2775:2775 -p 8088:88 eagafonov/smppsim:latest
```
### 2. Build the project

```bash
./gradlew build
```

### 3. Run the application

```bash
./gradlew bootRun
```

Expected startup logs:
```
Creating reactive MongoClient with URI: mongodb://127.0.0.1:27017
Configuring WebFlux Netty server on port: 9898
gRPC server started on port 50051
```

---

## Usage

### Place an order — gRPC

```bash
grpcurl -plaintext -d '{
  "orderId": "ORD-001",
  "customerId": "CUST-42",
  "customerPhoneNumber": "50588888888",
  "items": ["SIM Card", "Router"]
}' localhost:50051 OrderService/CreateOrder
```

Response:
```json
{
  "orderId": "ORD-001",
  "status": "PROCESSED"
}
```

---

### REST API

#### Get order status

```bash
GET http://localhost:9898/api/v1/orders/{orderId}/status

curl http://localhost:9898/api/v1/orders/ORD-001/status
```

Response `200 OK`:
```json
{
  "orderId": "ORD-001",
  "customerId": "CUST-42",
  "customerPhoneNumber": "50588888888",
  "status": "PROCESSED",
  "items": ["SIM Card", "Router"],
  "ts": "2026-04-08T17:00:00+00:00"
}
```

Response `404 Not Found`:
```json
{
  "timestamp": "2026-04-08T17:00:00+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found"
}
```

---

#### Count orders by date range

```bash
GET http://localhost:9898/api/v1/orders/count?from={ISO8601}&to={ISO8601}

curl "http://localhost:9898/api/v1/orders/count?from=2026-01-01T00:00:00+00:00&to=2026-12-31T23:59:59+00:00"
```

Response `200 OK`:
```json
{
  "from": "2026-01-01T00:00:00+00:00",
  "to": "2026-12-31T23:59:59+00:00",
  "totalOrders": 5
}
```

Response `400 Bad Request`:
```json
{
  "timestamp": "2026-04-08T17:00:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "'from' must be before 'to'"
}
```

> **Note:** Date-time values must be ISO-8601 with offset. The `+` character must be URL-encoded as `%2B` when using curl.

---

### Actuator & Metrics

| Endpoint | Description |
|---|---|
| `GET /actuator/health` | Application health |
| `GET /actuator/info` | Application info |
| `GET /actuator/prometheus` | Prometheus scrape endpoint |
| `GET /actuator/metrics` | All registered metrics |
| `GET /actuator/metrics/orders_received` | Orders received counter |
| `GET /actuator/metrics/orders_processed` | Orders processed counter |
| `GET /actuator/metrics/orders_error` | Orders error counter |

---