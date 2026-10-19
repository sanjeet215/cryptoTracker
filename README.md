# cryptoTracker

Real-time crypto market tracker: live trades from Binance flow through Kafka into Postgres,
with candles, price alerts and a live dashboard. Built to practise Spring Boot, Kafka and JPA
the way production systems use them.

## Services

| Service | Folder | What it does |
|---|---|---|
| ingestor-service | `ingestor-service/` | Binance WebSocket → Kafka (`trades.v1`) |
| trade-consumer | `trade-consumer/` | Kafka → JPA → Postgres |

## Port map (local dev)

One source of truth. Check here before giving a service a port.

| Component | Port | Notes |
|---|---|---|
| gateway | 8080 | Planned. Only entry point in prod-style setup |
| kafka-ui | 8081 | http://localhost:8081 |
| ingestor-service | 8082 | Actuator (ops) on 9082 |
| trade-consumer | 8083 | Planned |
| market-service | 8084 | Planned |
| alert-service | 8085 | Planned |
| Kafka | 9092 | Apps on the Mac. Containers use `kafka:29092` |
| Postgres | 5432 | Bound to 127.0.0.1 only |
| Redis | 6379 | Planned |

Actuator (health, metrics, loggers) runs on a separate port per service: 90xx, matching the app's 80xx.

Port taken? Find out who has it: `lsof -i :<port>`

In containers (later), every service listens on 8080 internally and is reached by
service name, e.g. `http://alert-service:8080`. Only the gateway is exposed.

## Kafka topics

| Topic | Partitions | Key | Purpose |
|---|---|---|---|
| `trades.v1` | 3 | symbol | Live trades |
| `trades.v1.DLT` | 1 | symbol | Messages that failed processing |
| `alerts.v1` | 3 | user id | Fired price alerts |

## Run locally

Prerequisites: Java 21, Colima (or Docker Desktop) with Docker Compose.

```bash
# 1. Infra
colima start
cd composefiles
cp .env.example .env          # first time only, then set your password
docker compose up -d

# 2. Services (from the repo root)
cd ..
./gradlew :ingestor-service:bootRun
```

Build everything: `./gradlew build` · Clean everything: `./gradlew clean`
