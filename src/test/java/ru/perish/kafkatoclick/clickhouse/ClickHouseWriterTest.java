package ru.perish.kafkatoclick.clickhouse;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import ru.perish.kafkatoclick.config.ImporterProperties;
import ru.perish.kafkatoclick.model.ImportEvent;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ClickHouseWriterTest {

    private static final String EXPECTED_SQL =
            "INSERT INTO events (id, source, payload, event_time) VALUES (?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final ClickHouseWriter writer =
            new ClickHouseWriter(jdbcTemplate, new ImporterProperties("events", "events"));

    @Test
    @SuppressWarnings("unchecked")
    void writesBatchWithGeneratedInsert() throws Exception {
        PreparedStatement statement = mock(PreparedStatement.class);
        when(jdbcTemplate.batchUpdate(eq(EXPECTED_SQL), any(List.class), anyInt(), any()))
                .thenAnswer(invocation -> {
                    List<ImportEvent> batch = invocation.getArgument(1);
                    ParameterizedPreparedStatementSetter<ImportEvent> setter = invocation.getArgument(3);
                    for (ImportEvent event : batch) {
                        setter.setValues(statement, event);
                    }
                    return new int[0][0];
                });

        Instant eventTime = Instant.parse("2024-01-01T00:00:00Z");
        writer.write(List.of(new ImportEvent("1", "test", "{\"a\":1}", eventTime)));

        verify(statement).setString(1, "1");
        verify(statement).setString(2, "test");
        verify(statement).setString(3, "{\"a\":1}");
        verify(statement).setTimestamp(4, Timestamp.from(eventTime));
    }

    @Test
    void skipsEmptyBatch() {
        writer.write(List.of());
        verifyNoInteractions(jdbcTemplate);
    }
}
