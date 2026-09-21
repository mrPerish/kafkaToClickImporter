package ru.perish.kafkatoclick.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.perish.kafkatoclick.config.ImporterProperties;

import java.nio.charset.StandardCharsets;

/**
 * Отправляет необработанные сообщения в dead letter topic вместе с причиной.
 */
@Component
public class DeadLetterPublisher {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ImporterProperties properties;
    private final MeterRegistry meterRegistry;

    public DeadLetterPublisher(KafkaTemplate<String, String> kafkaTemplate,
                               ImporterProperties properties,
                               MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Публикует исходное сообщение, сохраняя его заголовки и добавляя причину отбраковки.
     */
    public void send(ConsumerRecord<String, String> source, String reason, String detail) {
        String typeId = header(source);
        meterRegistry.counter("importer.messages.dead.lettered",
                "type", typeId == null ? "unknown" : typeId, "reason", reason).increment();

        ProducerRecord<String, String> record =
                new ProducerRecord<>(properties.deadLetterTopic(), source.key(), source.value());
        source.headers().forEach(record.headers()::add);
        record.headers().add("dlt-origin-topic", source.topic().getBytes(StandardCharsets.UTF_8));
        record.headers().add("dlt-origin-partition",
                String.valueOf(source.partition()).getBytes(StandardCharsets.UTF_8));
        record.headers().add("dlt-origin-offset", String.valueOf(source.offset()).getBytes(StandardCharsets.UTF_8));
        record.headers().add("dlt-reason", reason.getBytes(StandardCharsets.UTF_8));
        if (detail != null) {
            record.headers().add("dlt-detail", detail.getBytes(StandardCharsets.UTF_8));
        }

        try {
            kafkaTemplate.send(record).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DeadLetterPublishException(source, e);
        } catch (Exception e) {
            throw new DeadLetterPublishException(source, e);
        }
        log.warn("Sent offset {} of {} to {} ({})", source.offset(), source.topic(),
                properties.deadLetterTopic(), reason);
    }

    private String header(ConsumerRecord<String, String> source) {
        var typeId = source.headers().lastHeader("__TypeId__");
        return typeId == null ? null : new String(typeId.value(), StandardCharsets.UTF_8);
    }
}
