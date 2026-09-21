package ru.perish.kafkatoclick.kafka;

/**
 * Значение поля-дискриминатора не сопоставлено ни с одной таблицей.
 */
public class UnknownDiscriminatorException extends RuntimeException {

    public UnknownDiscriminatorException(String field, String value) {
        super("Unknown " + field + " " + value);
    }
}
