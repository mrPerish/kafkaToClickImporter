package ru.perish.kafkatoclick.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.perish.kafkatoclick.clickhouse.ClickHouseWriter;
import ru.perish.kafkatoclick.model.ImportEvent;

import java.util.List;

@Component
public class EventListener {

    private static final Logger log = LoggerFactory.getLogger(EventListener.class);

    private final ClickHouseWriter writer;

    public EventListener(ClickHouseWriter writer) {
        this.writer = writer;
    }

    @KafkaListener(topics = "${importer.topic}", batch = "true")
    public void onMessages(List<ImportEvent> events) {
        writer.write(events);
        log.info("Imported {} events into ClickHouse", events.size());
    }
}
