package ru.perish.kafkatoclick.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.perish.kafkatoclick.clickhouse.ClickHouseWriter;
import ru.perish.kafkatoclick.kafka.MessageRouter.RoutedMessage;
import ru.rtksoft.smev3.billing.dto.BillingData;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class BillingDataListener {

    private static final String TYPE_ID_HEADER = "__TypeId__";
    private static final Logger log = LoggerFactory.getLogger(BillingDataListener.class);

    private final MessageRouter router;
    private final ClickHouseWriter writer;

    public BillingDataListener(MessageRouter router, ClickHouseWriter writer) {
        this.router = router;
        this.writer = writer;
    }

    @KafkaListener(topics = "${importer.topic}")
    public void onMessages(List<ConsumerRecord<String, String>> records) {
        Map<TableAndType, List<BillingData>> batches = new LinkedHashMap<>();
        for (ConsumerRecord<String, String> record : records) {
            String typeId = typeId(record);
            try {
                RoutedMessage routed = router.route(typeId, record.value());
                batches.computeIfAbsent(new TableAndType(routed.table(), routed.type()), key -> new ArrayList<>())
                        .add(routed.data());
            } catch (UnknownMessageTypeException e) {
                log.error("Unknown __TypeId__ {}, offset {}", typeId, record.offset());
            } catch (UnknownDiscriminatorException e) {
                log.error("{} for __TypeId__ {}, offset {}", e.getMessage(), typeId, record.offset());
            } catch (Exception e) {
                log.error("Failed to parse message with __TypeId__ {}, offset {}", typeId, record.offset(), e);
            }
        }
        batches.forEach((target, rows) -> {
            writer.write(target.table(), target.type(), rows);
            log.info("Inserted {} rows into {}", rows.size(), target.table());
        });
    }

    private String typeId(ConsumerRecord<String, String> record) {
        Header header = record.headers().lastHeader(TYPE_ID_HEADER);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }

    private record TableAndType(String table, Class<? extends BillingData> type) {
    }
}
