# Krash - gRPC Chat Application

A real-time chat application built with Spring Boot, gRPC, and Kotlin. Messages are streamed via gRPC and persisted in PostgreSQL.

## How to Run ?

1) Prerequisites
- Java 24+
- PostgreSQL 16+ running on localhost:5432 (or override via `SPRING_DATASOURCE_URL` etc.)
- Docker & Docker Compose (optional)

2) Local run (server)
```bash
# You must have PostgreSQL running and a db named 'krash' created:
createdb krash

# start the server
./gradlew bootRun
```
Server listens on `localhost:9090`.

3) Local run (client)
```bash
./gradlew runClient
```

4) Docker Compose (Postgres + app)
```bash
docker-compose up --build
```
This starts PostgreSQL and the app; the app is exposed on `localhost:9090`.

## Database Schema
- Message: id, sender, content, timestamp, roomId
- ChatRoom: id, roomId, name, description, createdAt, isActive

## gRPC API
- `SendMessage(ChatMessage) returns (Empty)` — send a message to a room
- `StreamMessages(User) returns (stream ChatMessage)` — subscribe to messages in a room

## Build
```bash
./gradlew bootJar
```


