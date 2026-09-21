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
| `CLICKHOUSE_URL`, `CLICKHOUSE_USER`, `CLICKHOUSE_PASSWORD` | подключение к ClickHouse |

Маршруты задаются в `importer.routes`, ключ — значение заголовка `__TypeId__`:

```yaml
importer:
  routes:
    ExportChargesRequest:
      type: ru.rtksoft.smev3.billing.dto.ExportChargesRequestBillingData
      table: raw_smev3_export_charges_cc_req
      discriminator-field: ct_vvs          # необязательно
      tables-by-discriminator:             # значение поля -> таблица
        "urn://...cc": raw_smev3_export_charges_cc_req
```

Если `discriminator-field` не задан или значение не найдено, используется `table`.

## Локальный запуск

```bash
docker compose up -d          # Kafka + ClickHouse, таблицы создаются из sql/init.sql
mvn spring-boot:run
curl localhost:8080/actuator/health
```

## Устойчивость и метрики

Ошибка не роняет батч: сообщение с неизвестным `__TypeId__`/`ct_charge_type` или битым JSON логируется и пропускается, а если INSERT батча отвергнут ClickHouse (например, ограничение на столбец), строки вставляются по одной — сбойная логируется со всеми значениями, остальные записываются. Повторной обработки батча не происходит.

Метрики Micrometer (`/actuator/metrics`, `/actuator/prometheus`):

| Метрика | Теги | Смысл |
| --- | --- | --- |
| `importer.messages.consumed` | `type` | прочитано сообщений |
| `importer.messages.rejected` | `type`, `reason` | не разобрано (`unknown_type`, `unknown_discriminator`, `parse_error`) |
| `importer.rows.inserted` | `table`, `type` | записано строк |
| `importer.rows.failed` | `table`, `type` | потеряно строк на вставке |
| `importer.write` | `table`, `type` | время вставки батча |

## Тесты

```bash
mvn test
```
