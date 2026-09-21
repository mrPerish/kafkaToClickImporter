package ru.perish.kafkatoclick.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Сообщение не удалось отправить в dead letter topic: батч нельзя коммитить, иначе данные пропадут.
 */
public class DeadLetterPublishException extends RuntimeException {

    public DeadLetterPublishException(ConsumerRecord<String, String> source, Throwable cause) {
        super("Cannot publish offset " + source.offset() + " of " + source.topic() + " to dead letter topic", cause);
    }
}
