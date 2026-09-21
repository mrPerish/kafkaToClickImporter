package ru.perish.kafkatoclick.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record ImportEvent(String id, String source, String payload,
                          @JsonFormat(shape = JsonFormat.Shape.STRING) Instant eventTime) {
}
