package ru.perish.kafkatoclick.clickhouse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.perish.kafkatoclick.config.ImporterProperties;
import ru.perish.kafkatoclick.model.ImportEvent;

import java.sql.Timestamp;
import java.util.List;

@Component
public class ClickHouseWriter {

    private final JdbcTemplate jdbcTemplate;
    private final String insertSql;

    public ClickHouseWriter(JdbcTemplate jdbcTemplate, ImporterProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertSql = "INSERT INTO " + properties.table() + " (id, source, payload, event_time) VALUES (?, ?, ?, ?)";
    }

    public void write(List<ImportEvent> events) {
        if (events.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(insertSql, events, events.size(), (ps, event) -> {
            ps.setString(1, event.id());
            ps.setString(2, event.source());
            ps.setString(3, event.payload());
            ps.setTimestamp(4, Timestamp.from(event.eventTime()));
        });
    }
}
