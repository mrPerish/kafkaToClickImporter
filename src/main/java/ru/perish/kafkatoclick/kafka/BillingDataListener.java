package ru.perish.kafkatoclick.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.perish.kafkatoclick.clickhouse.ClickHouseWriter;
import ru.perish.kafkatoclick.clickhouse.ClickHouseWriter.WriteResult;
import ru.perish.kafkatoclick.kafka.MessageRouter.RoutedMessage;
import ru.rtksoft.smev3.billing.dto.BillingData;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Батчевое чтение топика: роутинг по {@code __TypeId__} и пакетная запись в ClickHouse.
 *
 * <p>Сообщение, которое не удалось разобрать или записать, уходит в dead letter topic; батч
 * при этом не переобрабатывается. Исключение бросается только если недоступен и сам DLT —
 * тогда оффсеты не коммитятся и данные не теряются.
 */
@Component
public class BillingDataListener {

    private static final String TYPE_ID_HEADER = "__TypeId__";
    private static final String UNKNOWN_TYPE = "unknown";
    private static final Logger log = LoggerFactory.getLogger(BillingDataListener.class);

    private final MessageRouter router;
    private final ClickHouseWriter writer;
    private final DeadLetterPublisher deadLetterPublisher;
    private final MeterRegistry meterRegistry;

    public BillingDataListener(MessageRouter router,
                               ClickHouseWriter writer,
                               DeadLetterPublisher deadLetterPublisher,
                               MeterRegistry meterRegistry) {
        this.router = router;
        this.writer = writer;
        this.deadLetterPublisher = deadLetterPublisher;
        this.meterRegistry = meterRegistry;
    }

    @KafkaListener(topics = "${importer.topic}")
    public void onMessages(List<ConsumerRecord<String, String>> records) {
        Map<TableAndType, List<BillingData>> batches = new LinkedHashMap<>();
        Map<BillingData, ConsumerRecord<String, String>> sources = new IdentityHashMap<>();

        for (ConsumerRecord<String, String> record : records) {
            String typeId = typeId(record);
            meterRegistry.counter("importer.messages.consumed", "type", typeId == null ? UNKNOWN_TYPE : typeId)
                    .increment();
            try {
                RoutedMessage routed = router.route(typeId, record.value());
                batches.computeIfAbsent(new TableAndType(routed.table(), routed.type()), key -> new ArrayList<>())
                        .add(routed.data());
                sources.put(routed.data(), record);
            } catch (UnknownMessageTypeException e) {
                log.error("Unknown __TypeId__ {}, offset {}", typeId, record.offset());
                deadLetterPublisher.send(record, "unknown_type", e.getMessage());
            } catch (UnknownDiscriminatorException e) {
                log.error("{} for __TypeId__ {}, offset {}", e.getMessage(), typeId, record.offset());
                deadLetterPublisher.send(record, "unknown_discriminator", e.getMessage());
            } catch (Exception e) {
                log.error("Failed to parse message with __TypeId__ {}, offset {}", typeId, record.offset(), e);
                deadLetterPublisher.send(record, "parse_error", e.getMessage());
            }
        }

        batches.forEach((target, rows) -> {
            WriteResult result = writer.write(target.table(), target.type(), rows);
            result.failed().forEach(row -> deadLetterPublisher.send(sources.get(row), "insert_error", target.table()));
            log.info("Inserted {} rows into {}, dead lettered {}",
                    result.inserted(), target.table(), result.failed().size());
        });
    }

    private String typeId(ConsumerRecord<String, String> record) {
        Header header = record.headers().lastHeader(TYPE_ID_HEADER);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }

    private record TableAndType(String table, Class<? extends BillingData> type) {
    }
}
