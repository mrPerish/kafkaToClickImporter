# kafkaToClickImporter

Сервис на Java 17 + Spring Boot: читает JSON-сообщения биллинга СМЭВ3 из одного топика Kafka и пакетно пишет их в таблицы ClickHouse.

## Как устроено

- `kafka/BillingDataListener` — батчевый `@KafkaListener` на топик `importer.topic`; группирует записи батча по целевой таблице.
- `kafka/MessageRouter` — по заголовку `__TypeId__` находит маршрут в конфиге, разбирает JSON в нужный DTO и выбирает таблицу.
- `clickhouse/ColumnRegistry` — колонки берутся из полей DTO: значение `@JsonProperty`, иначе имя поля.
- `clickhouse/ClickHouseWriter` — `JdbcTemplate.batchUpdate` с INSERT по этим колонкам.
- DTO — `ru.rtksoft.smev3.billing.dto.*`.

## Конфигурация

Всё в `src/main/resources/application.yml`, значения переопределяются переменными окружения:

| Переменная | Назначение |
| --- | --- |
| `KAFKA_BOOTSTRAP_SERVERS` | брокеры Kafka |
| `KAFKA_GROUP_ID` | consumer group |
| `KAFKA_MAX_POLL_RECORDS` | размер батча |
| `IMPORTER_TOPIC` | топик с сообщениями |
| `IMPORTER_DEAD_LETTER_TOPIC` | топик для неразобранных/незаписанных сообщений |
| `CLICKHOUSE_URL`, `CLICKHOUSE_USER`, `CLICKHOUSE_PASSWORD` | подключение к ClickHouse |

Маршруты задаются в `importer.routes`, ключ — значение заголовка `__TypeId__`:

```yaml
importer:
  routes:
    ExportChargesRequest:
      type: ru.rtksoft.smev3.billing.dto.ExportChargesRequestBillingData
      discriminator-field: ct_charge_type   # необязательно
      tables-by-discriminator:              # значение поля -> таблица
        ChargesConditions: raw_smev3_export_charges_cc_req
        PayersConditions: raw_smev3_export_charges_pc_req
        TimeConditions: raw_smev3_export_charges_tc_req
```

Без `discriminator-field` используется `table`. С ним таблица берётся только из `tables-by-discriminator`: неизвестное или пустое значение — ошибка, сообщение уходит в DLT.

## Локальный запуск

```bash
docker compose up -d          # Kafka + ClickHouse, таблицы создаются из sql/init.sql
mvn spring-boot:run
curl localhost:8080/actuator/health
```

## Устойчивость и метрики

Ошибка не роняет батч: сообщение с неизвестным `__TypeId__`/`ct_charge_type` или битым JSON логируется и уходит в dead letter topic, а если INSERT батча отвергнут ClickHouse (например, ограничение на столбец), строки вставляются по одной — сбойные уходят в DLT, остальные записываются. Повторной обработки батча не происходит.

В DLT кладётся исходное сообщение со своими заголовками плюс `dlt-reason` (`unknown_type`, `unknown_discriminator`, `parse_error`, `insert_error`), `dlt-detail`, `dlt-origin-topic/partition/offset`. Если недоступен сам DLT, бросается исключение и оффсеты не коммитятся — данные не теряются.

Метрики Micrometer (`/actuator/metrics`, `/actuator/prometheus`):

| Метрика | Теги | Смысл |
| --- | --- | --- |
| `importer.messages.consumed` | `type` | прочитано сообщений |
| `importer.messages.dead.lettered` | `type`, `reason` | отправлено в DLT |
| `importer.rows.inserted` | `table`, `type` | записано строк |
| `importer.rows.failed` | `table`, `type` | строк отвергнуто ClickHouse |
| `importer.write` | `table`, `type` | время вставки батча |

## Тесты

```bash
mvn test
```
