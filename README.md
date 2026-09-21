# kafkaToClickImporter

Сервис на Java 21 + Spring Boot: читает JSON-события из Kafka и пакетно пишет их в ClickHouse.

## Как устроено

- `kafka/EventListener` — батчевый `@KafkaListener` на топик `importer.topic`.
- `clickhouse/ClickHouseWriter` — `JdbcTemplate.batchUpdate` в таблицу `importer.table`.
- `model/ImportEvent` — формат сообщения: `{"id":"1","source":"app","payload":"{}","eventTime":"2024-01-01T00:00:00Z"}`.
- Настройки — в `src/main/resources/application.yml`, все значения переопределяются переменными окружения.

## Локальный запуск

```bash
docker compose up -d          # Kafka + ClickHouse, таблица events создаётся из sql/init.sql
./mvnw spring-boot:run
```

Проверка:

```bash
curl localhost:8080/actuator/health
```

## Тесты

```bash
./mvnw test
```
