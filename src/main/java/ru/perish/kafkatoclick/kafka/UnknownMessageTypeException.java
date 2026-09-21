package ru.perish.kafkatoclick.kafka;

public class UnknownMessageTypeException extends RuntimeException {

    public UnknownMessageTypeException(String typeId) {
        super("Unknown __TypeId__ " + typeId);
    }
}
